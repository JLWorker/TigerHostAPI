package tgc.plus.proxmoxservice.configs.utils;


import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class JwtServerWebExchangeMatcher implements ServerWebExchangeMatcher {
    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return Mono.defer(() -> Mono.just(exchange)
                .filter(key -> key.getRequest().getHeaders().containsKey("Authorization"))
                .flatMap(el -> MatchResult.match())
                .switchIfEmpty(MatchResult.notMatch()));
    }
}
