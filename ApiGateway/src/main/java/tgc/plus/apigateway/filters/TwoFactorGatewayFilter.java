package tgc.plus.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.jwt_claims_dto.TwoFactorTokenClaimsDTO;
import tgc.plus.apigateway.filters.utils.FiltersUtils;
import tgc.plus.apigateway.filters.utils.TokenRequestHeader;

@Component
@Slf4j
public class TwoFactorGatewayFilter implements GatewayFilter {

    @Autowired
    private FiltersUtils filtersUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(TokenRequestHeader.TWO_FACTOR.getHeaderName());

        return Mono.defer(() -> {
        if (token != null && !token.isBlank())
            return filtersUtils.getTokenClaimsData(token, TwoFactorTokenClaimsDTO.class)
                    .filter(twoFactorTokenClaims -> !twoFactorTokenClaims.getDeviceToken().isBlank())
                    .switchIfEmpty(filtersUtils.getInvalidTokenException())
                    .then();
        else
            return filtersUtils.getInvalidTokenException();
        });
    }
}
