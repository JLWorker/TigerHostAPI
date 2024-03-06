package tgc.plus.feedbackgateaway.configs.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.feedbackgateaway.exceptions.AccessTokenExpiredException;
import tgc.plus.feedbackgateaway.exceptions.AuthException;

import javax.crypto.SecretKey;
import java.util.List;


@Service
@Slf4j
public class TokenService {

    @Value("${jwt.token.secret}")
    private String secretKey;

    private SecretKey key;

    @PostConstruct
    void init(){
       key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }


    public Mono<Authentication> getAuthentication(String token) {
        return Mono.defer(()->{
            try {
                Jws<Claims> claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
                String userCode = claims.getPayload().get("user_code", String.class);
                String role = claims.getPayload().get("role", String.class);
                if ((userCode == null || userCode.isBlank()) || (role == null || role.isBlank())) {
                    throw new AuthException("Invalid access token");
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
                return Mono.error(new AuthException("Invalid access token"));
        }).doOnError(e->log.error(e.getMessage()));
    }

}
