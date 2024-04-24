package tgc.plus.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.jwt_claims_dto.AccessTokenClaimsDTO;
import tgc.plus.apigateway.filters.utils.FiltersUtils;
import tgc.plus.apigateway.filters.utils.TokenRequestHeader;

@Component
@Slf4j
public class JwtGatewayFilter implements GatewayFilter {

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
                                .switchIfEmpty(filtersUtils.getInvalidTokenException())
                                .flatMap(claims -> chain.filter(exchange));
                    } else
                        return filtersUtils.getInvalidTokenException();
                });
    }
}
