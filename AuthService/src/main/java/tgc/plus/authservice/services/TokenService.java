package tgc.plus.authservice.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.netty.util.internal.ThreadLocalRandom;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaimsDto;
import tgc.plus.authservice.dto.jwt_claims_dto.TokenClaimsDto;
import tgc.plus.authservice.dto.jwt_claims_dto.TwoFactorTokenClaimsDto;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.AuthTokenExpiredException;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.AuthTokenInvalidException;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.RefreshTokenException;
import tgc.plus.authservice.exceptions.exceptions_elements.two_factor_exceptions.TwoFactorTokenException;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.utils.factories.TokenDateFactory;
import tgc.plus.authservice.services.utils.utils_enums.TokenType;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.security.refresh.expired.days}")
    public Integer refreshSecurityExpireDate;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private final UserTokenRepository userTokenRepository;
    private final ObjectMapper mapper;
    private final TokenDateFactory tokenDateFactory;

    private SecretKey key;

    @PostConstruct
    void init(){
       key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Mono<String> createAccessToken(TokenClaimsDto tokenClaimsDto, TokenType tokenType) {
            return tokenDateFactory.getTokenDateByType(tokenType)
                    .flatMap(tokenExpired -> {
                        Instant now = Instant.now();
                        Instant expirationDate = now.plusMillis(tokenExpired);
                        Map<String, String> claims = mapper.convertValue(tokenClaimsDto, new TypeReference<>() {});
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


    private <T extends TokenClaimsDto> Mono<T> getTokenData(String token, Class<T> targetClass) {
        return Mono.defer(() -> {
                    Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                    return Mono.just(mapper.convertValue(claims.getPayload(), targetClass));
        });
    }

    public Mono<Authentication> getAuthentication(String token) {
            return getTokenData(token, AccessTokenClaimsDto.class)
                    .filter(accessTokenClaimsDto -> !accessTokenClaimsDto.getUserCode().isBlank()
                                || !accessTokenClaimsDto.getRole().isBlank())
                    .switchIfEmpty(getAuthTokenInvalidException())
                    .flatMap(accessTokenClaimsDto -> {
                        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(accessTokenClaimsDto.getRole()));
                        return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(accessTokenClaimsDto.getUserCode(), "", authorities));
                    })
                    .onErrorResume(e -> {
                        if (e instanceof ExpiredJwtException)
                            return getAuthTokenExpiredException();
                        else
                            return getAuthTokenInvalidException();
                    });
        }


    public Mono<String> get2FaTokenData(String token) {
        return getTokenData(token, TwoFactorTokenClaimsDto.class)
                .filter(twoFactorTokenClaimsDto -> !twoFactorTokenClaimsDto.getDeviceToken().isBlank())
                .switchIfEmpty(getAuthTokenInvalidException())
                .flatMap(twoFactorTokenClaimsDto -> Mono.just(twoFactorTokenClaimsDto.getDeviceToken()))
                .onErrorResume(e -> get2FaTokenException());
    }

    public Mono<String> getTokenFromRequest(ServerWebExchange serverWebExchange){
        return Mono.defer(()->{
            String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
            String bearer = "Bearer_";
            if (token != null && token.startsWith(bearer)){
                return Mono.just(token.substring(bearer.length()));
            }
            else
                return getAuthTokenInvalidException();
        });
    }


    public Mono<Tuple2<String, UserToken>> updatePairTokens(TokenClaimsDto tokenClaimsDto, String refreshToken, Instant expiredDate){
        if (expiredDate.isBefore(Instant.now()))
            return userTokenRepository.removeUserTokenByRefreshToken(refreshToken)
                    .then(getRefreshTokenException());
        else {
            String newRefreshToken = UUID.randomUUID().toString();
            Instant createDate = Instant.now();
            Instant newExpiredDate = createDate.plusMillis(Duration.ofDays(refreshSecurityExpireDate).toMillis());
            return createAccessToken(tokenClaimsDto, TokenType.SECURITY)
                    .zipWith(userTokenRepository.updateRefreshToken(refreshToken, newRefreshToken, newExpiredDate, createDate)
                            .filter(Objects::nonNull)
                            .switchIfEmpty(getRefreshTokenException()));
        }
    }

    public  <T> Mono<T> getRefreshTokenException(){
        return Mono.error(new RefreshTokenException("Refresh token not exist"));
    }

    public <T> Mono<T> getAuthTokenInvalidException(){
        return Mono.error(new AuthTokenInvalidException("Token invalid"));
    }

    public <T> Mono<T> getAuthTokenExpiredException(){
        return Mono.error(new AuthTokenExpiredException("Token expired"));
    }

    public <T> Mono<T> get2FaTokenException(){
        return Mono.error(new TwoFactorTokenException("Token invalid"));
    }



}
