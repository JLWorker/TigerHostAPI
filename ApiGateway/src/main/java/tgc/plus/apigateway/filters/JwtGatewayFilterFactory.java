package tgc.plus.apigateway.filters;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.ResponseException;
import tgc.plus.apigateway.exceptions.AccessTokenCorruptionException;
import tgc.plus.apigateway.exceptions.AccessTokenExpiredException;
import tgc.plus.apigateway.filters.utils.FiltersUtils;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtGatewayFilterFactory implements GatewayFilter {

    @Value("${jwt.secret}")
    private String jwtSecret;
    private SecretKey secretKey;

    @Autowired
    FiltersUtils filtersUtils;

    @PostConstruct
    void init(){
       secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String jwtToken = exchange.getRequest().getHeaders().getFirst("Authorization");
        String headerStart = "Bearer_";

        if (jwtToken != null && jwtToken.startsWith(headerStart)){
            try {
                Jws<Claims> claimsJwt = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtToken.substring(headerStart.length()));
                String userCode = claimsJwt.getPayload().get("user_code", String.class);
                String role = claimsJwt.getPayload().get("role", String.class);
                if (userCode==null || userCode.isBlank() || role==null || role.isBlank())
                    return Mono.error(new AccessTokenCorruptionException("Access token is empty"));
                else
                    return chain.filter(exchange);
            }
            catch (Exception e){
                RuntimeException exception;
                if (e instanceof ExpiredJwtException) {
                    exception = new AccessTokenExpiredException("Access token expired");
                    exchange.getResponse().getHeaders().add("Expired", "true");
                }
                else {
                    log.error(e.getMessage());
                    exception = new AccessTokenCorruptionException("Failed to convert token");
                    exchange.getResponse().getHeaders().add("Logout", "true");
                }
                return filtersUtils.getErrorResponse(exchange, exception)
                        .flatMap(chain::filter);

            }

        }
        return chain.filter(exchange);
    }

}
