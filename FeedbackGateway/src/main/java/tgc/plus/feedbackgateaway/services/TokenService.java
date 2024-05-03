package tgc.plus.feedbackgateaway.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
import tgc.plus.feedbackgateaway.dto.jwt_claims_dto.AccessTokenClaimsDto;
import tgc.plus.feedbackgateaway.dto.jwt_claims_dto.TokenClaimsDto;
import tgc.plus.feedbackgateaway.exceptions.auth_exceptions.AuthTokenExpiredException;
import tgc.plus.feedbackgateaway.exceptions.auth_exceptions.AuthTokenInvalidException;

import javax.crypto.SecretKey;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    @Value("${jwt.token.secret}")
    private String secretKey;

    private final ObjectMapper mapper;

    private SecretKey key;

    @PostConstruct
    void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
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


    public Mono<String> getTokenFromRequest(ServerWebExchange serverWebExchange) {
        return Mono.defer(() -> {
            String token = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");
            String bearer = "Bearer_";
            if (token != null && token.startsWith(bearer)) {
                return Mono.just(token.substring(bearer.length()));
            } else
                return getAuthTokenInvalidException();
        });
    }

    public <T> Mono<T> getAuthTokenInvalidException() {
        return Mono.error(new AuthTokenInvalidException("Token invalid"));
    }

    public <T> Mono<T> getAuthTokenExpiredException() {
        return Mono.error(new AuthTokenExpiredException("Token expired"));
    }
}
