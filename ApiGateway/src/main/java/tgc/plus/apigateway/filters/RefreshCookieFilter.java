package tgc.plus.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.exceptions.cookie_exceptions.InvalidCookieException;
import tgc.plus.apigateway.exceptions.cookie_exceptions.MissingCookieException;
import tgc.plus.apigateway.filters.utils.utils_enums.CookiePayload;

@Component
public class RefreshCookieFilter implements GatewayFilter, Ordered {

    private final String refreshTokenRegex = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpCookie httpCookie = exchange.getRequest().getCookies().getFirst(CookiePayload.REFRESH_TOKEN.name());
        if (httpCookie == null){
            return Mono.error(new MissingCookieException(String.format("Required cookie '%s' is not present", CookiePayload.REFRESH_TOKEN.name())));
        }
        else
            if (!httpCookie.getValue().matches(refreshTokenRegex)){
                return Mono.error(new InvalidCookieException("Invalid cookie"));
            }
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
