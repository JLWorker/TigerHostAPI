package tgc.plus.apigateway.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.jwt_claims_dto.TwoFactorTokenClaimsDto;
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidTwoFactorTokenException;
import tgc.plus.apigateway.filters.utils.FiltersUtils;
import tgc.plus.apigateway.filters.utils.utils_enums.TokenRequestHeader;

@Component
@Slf4j
public class TwoFactorGatewayFilter implements GatewayFilter, Ordered {

    @Autowired
    private FiltersUtils filtersUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return Mono.defer(() -> {
            String token = exchange.getRequest().getHeaders().getFirst(TokenRequestHeader.TWO_FACTOR.getHeaderName());
            if (token != null && !token.isBlank())
                return filtersUtils.getTokenClaimsData(token, TwoFactorTokenClaimsDto.class)
                    .filter(twoFactorTokenClaims -> !twoFactorTokenClaims.getDeviceToken().isBlank())
                    .switchIfEmpty(filtersUtils.getInvalidTwoFactorTokenException())
                    .then(chain.filter(exchange));
            else
                return filtersUtils.getInvalidTwoFactorTokenException();
        })
        .onErrorResume(e -> {
            if (!(e instanceof InvalidTwoFactorTokenException))
                return filtersUtils.getInvalidTwoFactorTokenException();
            else
                return Mono.error(e);
        });
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
