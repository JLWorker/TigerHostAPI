package tgc.plus.apigateway.filters.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.jwt_claims_dto.TokenClaimsDTO;
import tgc.plus.apigateway.exceptions.ExpiredTokenException;
import tgc.plus.apigateway.exceptions.InvalidTokenException;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Component
public class FiltersUtils {

    @Autowired
    private ObjectMapper objectMapper;

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String jwtSecret;
    @PostConstruct
    void init(){
        secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public <T extends TokenClaimsDTO> Mono<T> getTokenClaimsData(String token, Class<T> targetType) {
        return Mono.defer(() -> {
            Jws<Claims> claimsJwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return Mono.just(objectMapper.convertValue(claimsJwt.getPayload(), targetType));
        })
        .onErrorResume(e -> {
            if (!(e instanceof ExpiredJwtException))
                return getInvalidTokenException();
            else
                return Mono.error(new ExpiredTokenException("Token expired"));
        });
    }

    public <T> Mono<T> getInvalidTokenException(){
        return Mono.error(new InvalidTokenException("Token invalid"));
    }


}
