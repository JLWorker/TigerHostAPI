package tgc.plus.authservice.configs.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.netty.util.internal.ThreadLocalRandom;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.exceptions.exceptions_clases.AccessTokenExpiredException;
import tgc.plus.authservice.exceptions.exceptions_clases.AuthException;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.utils.RoleList;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.token.expired}")
    public Long accessExpireDate;

    @Value("${jwt.refresh.expired}")
    public Long refreshExpireDate;

    @Value("${jwt.token.secret}")
    private String secretKey;

    @Autowired
    private UserTokenRepository userTokenRepository;

    private SecretKey key;

    @PostConstruct
    void init(){
       key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Mono<String> createAccessToken(String userCode, String role) {
        return Mono.defer(() -> {
            Instant now = Instant.now();
            Instant expirationDate = now.plusMillis(accessExpireDate);
            Claims claims = Jwts.claims().add(Map.of("user_code", userCode, "role", role)).build();
            String accessToken = Jwts.builder()
                    .claims(claims)
                    .expiration(Date.from(expirationDate))
                    .issuedAt(Date.from(now))
                    .signWith(key)
                    .compact();
            return Mono.just(accessToken);
        });
    }

    public Mono<tgc.plus.authservice.entity.UserToken> createRefToken(Long userId) {
            return generateTokenId().flatMap(tokenId -> {
                String refreshToken = UUID.randomUUID().toString();
                Instant startDate = Instant.now();
                Instant finishDate = startDate.plusMillis(refreshExpireDate);
                return userTokenRepository.save(new tgc.plus.authservice.entity.UserToken(tokenId, userId, refreshToken, startDate, finishDate));
            });
        }

    private Mono<String> generateTokenId(){
       return Mono.defer(()->{
           int randInt = ThreadLocalRandom.current().nextInt(1000, 10000);
           return Mono.just("ID-" + Instant.now().getNano() + randInt);
       });
    }


    public Mono<Boolean> checkAccessToken(String token) throws AccessTokenExpiredException {
        return Mono.defer(() -> {
            try {
                Jws<Claims> claims = Jwts.parser().decryptWith(key).build().parseSignedClaims(token);
                return Mono.just(claims.getPayload().getExpiration().after(Date.from(Instant.now())));
            } catch (JwtException e) {
                return Mono.error(new InvalidRequestException("Invalid access token"));
            }
        });
    }

    public Mono<String> getUserCode(String token){
        return Mono.defer(() -> {
            try{
                Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                return Mono.just(claims.getPayload().get("user_code", String.class));
            }
            catch (JwtException e) {
                    return Mono.error(new JwtException(e.getMessage()));
            }
        });
    }

    public Mono<Authentication> getAuthentication(String token) {
        return Mono.defer(()->{
            try {
                Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                String userCode = claims.getPayload().get("user_code", String.class);
                String role = claims.getPayload().get("role", String.class);
                if ((userCode == null || userCode.isBlank()) || (role == null || role.isBlank())) {
                    return Mono.error(new AuthException("Invalid access token"));
                } else {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(claims.getPayload().get("role", String.class)));
                    return Mono.just((Authentication) new UsernamePasswordAuthenticationToken(userCode, "", authorities));
                }
            }
            catch (JwtException e) {
                if (e instanceof ExpiredJwtException)
                    return Mono.error(new AccessTokenExpiredException("Token expired"));
                else
                    return Mono.error((new AuthException("Invalid access token")));
            }
        }).doOnError(e -> log.error(e.getMessage()));
    }

    public Mono<String> getTokenFromRequest(ServerWebExchange serverWebExchange){
        return Mono.defer(()->{
            String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
            String bearer = "Bearer_";
            if (token != null && token.startsWith(bearer)){
                return Mono.just(token.substring(bearer.length()));
            }
            else
                return Mono.error(new AuthException("Invalid access token"));
        }).doOnError(e->log.error(e.getMessage()));
    }




//    public boolean checkRefreshToken(String username) throws TokenException{
//        User user = userRepository.findUserByUserName(username);
//        Date expired = user.getRefreshToken().getExpiredDate();
//        if(expired.before(new Date())){
//            return false;
//        }
//        else{
//            return true;
//        }
//    }


}
