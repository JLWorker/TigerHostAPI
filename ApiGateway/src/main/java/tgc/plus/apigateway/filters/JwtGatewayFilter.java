package tgc.plus.apigateway.filters;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.jwt_claims_dto.AccessTokenClaimsDTO;
import tgc.plus.apigateway.exceptions.token_exceptions.ExpiredAccessTokenException;
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidAccessTokenException;
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidTwoFactorTokenException;
import tgc.plus.apigateway.filters.utils.FiltersUtils;
import tgc.plus.apigateway.filters.utils.utils_enums.TokenRequestHeader;

@Component
@Slf4j
public class JwtGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    private FiltersUtils filtersUtils;

    private final String headerStart = "Bearer_";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.defer(() -> {
                    String jwtToken = exchange.getRequest().getHeaders().getFirst(TokenRequestHeader.ACCESS.getHeaderName());
                    if (jwtToken != null && jwtToken.startsWith(headerStart)) {
                        return filtersUtils.getTokenClaimsData(jwtToken.substring(headerStart.length()), AccessTokenClaimsDTO.class)
                                .filter(claims -> !claims.getUserCode().isBlank() || !claims.getRole().isBlank())
                                .switchIfEmpty(filtersUtils.getInvalidAccessTokenException())
                                .flatMap(claims -> chain.filter(exchange));
                    } else
                        return filtersUtils.getInvalidAccessTokenException();
                })
                .onErrorResume(e -> {
                    if (e instanceof ExpiredJwtException)
                        return Mono.error(new ExpiredAccessTokenException("Token expired"));
                    else if (!(e instanceof InvalidAccessTokenException))
                        return filtersUtils.getInvalidAccessTokenException();
                    else
                        return Mono.error(e);
                });
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
