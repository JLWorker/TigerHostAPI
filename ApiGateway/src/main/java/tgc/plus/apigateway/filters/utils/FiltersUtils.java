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
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidAccessTokenException;
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidTwoFactorTokenException;

import javax.crypto.SecretKey;

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
        });
    }

    public <T> Mono<T> getInvalidAccessTokenException(){
        return Mono.error(new InvalidAccessTokenException());
    }
    public <T> Mono<T> getInvalidTwoFactorTokenException(){
        return Mono.error(new InvalidTwoFactorTokenException());
    }


}
