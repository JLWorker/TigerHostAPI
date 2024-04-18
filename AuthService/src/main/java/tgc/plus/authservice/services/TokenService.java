package tgc.plus.authservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.netty.util.internal.ThreadLocalRandom;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.jwt_claims_dto.TokenClaims;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.AccessTokenExpiredException;
import tgc.plus.authservice.exceptions.exceptions_elements.AccessTokenCorruptionException;
import tgc.plus.authservice.exceptions.exceptions_elements.RefreshTokenException;
import tgc.plus.authservice.exceptions.exceptions_elements.TwoFactorTokenException;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.utils.TokenExpiredDate;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
public class TokenService {

    @Value("${jwt.security.access.expired.ms}")
    public Long accessSecurityExpireDate;

    @Value("${jwt.security.refresh.expired.days}")
    public Integer refreshSecurityExpireDate;

    @Value("${jwt.2fa.access.expired.ms}")
    public Long access2FaExpiredDate;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private ObjectMapper mapper;

    private SecretKey key;

    @PostConstruct
    void init(){
       key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Mono<String> createAccessToken(TokenClaims tokenClaims, String expiredType) {
        return Mono.defer(() -> {
            Instant now = Instant.now();
            long tokenExpired = switch (expiredType) {
                case "secure" -> accessSecurityExpireDate;
                case "2fa" -> access2FaExpiredDate;
                default -> 0L;
            };

            Instant expirationDate = now.plusMillis(tokenExpired);
            Map<String, String> claims = mapper.convertValue(tokenClaims, new TypeReference<>() {});
            String accessToken = Jwts.builder()
                    .claims(claims)
                    .expiration(Date.from(expirationDate))
                    .issuedAt(Date.from(now))
                    .signWith(key)
                    .compact();
            return Mono.just(accessToken);
        });
    }

    public Mono<UserToken> createRefToken(Long userId) {
            return generateTokenId().flatMap(tokenId -> {
                String refreshToken = UUID.randomUUID().toString();
                Instant startDate = Instant.now();
                Instant finishDate = startDate.plus(Duration.ofDays(refreshSecurityExpireDate));
                return userTokenRepository.save(new tgc.plus.authservice.entities.UserToken(tokenId, userId, refreshToken, finishDate, startDate));
            });
        }

    private Mono<String> generateTokenId(){
       return Mono.defer(()->{
           int randInt = ThreadLocalRandom.current().nextInt(1000, 10000);
           return Mono.just("ID-" + Instant.now().getNano() + randInt);
       });
    }

    public Mono<Authentication> getAuthentication(String token) {
        return Mono.defer(()->{
            try {
                Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                String userCode = claims.getPayload().get("user_code", String.class);
                String role = claims.getPayload().get("role", String.class);
                if ((userCode == null || userCode.isBlank()) || (role == null || role.isBlank())) {
                    throw new AccessTokenCorruptionException("Invalid access token");
                } else {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claims.getPayload().get("role", String.class)));
                    return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(userCode, "", authorities));
                }
            }
            catch (JwtException e) {
                if (e instanceof ExpiredJwtException)
                    return Mono.error(new AccessTokenExpiredException("Token expired"));
                else
                    return Mono.error((new AccessTokenCorruptionException(e.getMessage())));
            }
        });
    }

    public Mono<String> get2FaTokenData(String token) {
        return Mono.defer(()->{
            try {
                Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                String deviceToken = claims.getPayload().get("device_token", String.class);
                if(deviceToken==null || deviceToken.isBlank())
                    throw new TwoFactorTokenException("Invalid 2fa token");
                else
                    return Mono.just(deviceToken);
            }
            catch (JwtException e) {
                if (e instanceof ExpiredJwtException)
                    return Mono.error(new TwoFactorTokenException("Session time has expired"));
                else
                    return Mono.error(new TwoFactorTokenException("Invalid 2fa token"));
            }
        });
    }

    public Mono<String> getTokenFromRequest(ServerWebExchange serverWebExchange){
        return Mono.defer(()->{
            String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
            String bearer = "Bearer_";
            if (token != null && token.startsWith(bearer)){
                return Mono.just(token.substring(bearer.length()));
            }
            else
                return Mono.error(new AccessTokenCorruptionException("Invalid access token"));
        }).doOnError(e->log.error(e.getMessage()));
    }


    public Mono<UpdateTokenResponse> updatePairTokens(TokenClaims tokenClaims, String refreshToken, Instant expiredDate){
        if (expiredDate.isBefore(Instant.now()))
            return userTokenRepository.removeUserTokenByRefreshToken(refreshToken)
                    .then(getRefreshTokenException());
        else {
            String newRefreshToken = UUID.randomUUID().toString();
            Instant createDate = Instant.now();
            Instant finishDate = createDate.plusMillis(Duration.ofDays(refreshSecurityExpireDate).toMillis());
            return createAccessToken(tokenClaims, TokenExpiredDate.SECURITY.getName())
                    .zipWith(userTokenRepository.updateRefreshToken(refreshToken, newRefreshToken, finishDate, createDate)
                            .filter(res-> res!=0)
                            .switchIfEmpty(getRefreshTokenException()))
                    .flatMap(tokens -> Mono.just(new UpdateTokenResponse(tokens.getT1(), newRefreshToken)));
        }
    }

    private <T> Mono<T> getRefreshTokenException(){
        return Mono.error(new RefreshTokenException("Refresh token not exist"));
    }

}
