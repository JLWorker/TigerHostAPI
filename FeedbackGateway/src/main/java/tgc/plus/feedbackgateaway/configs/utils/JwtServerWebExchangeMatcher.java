package tgc.plus.feedbackgateaway.configs.utils;


import lombok.RequiredArgsConstructor;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtServerWebExchangeMatcher implements ServerWebExchangeMatcher {

    private final ServerWebExchangeMatcher permitAllWebExchangeMather;

    @Override
    public Mono<MatchResult> matches(ServerWebExchange exchange) {
        return permitAllWebExchangeMather.matches(exchange)
                .flatMap(matchResult -> (!matchResult.isMatch()) ?
                        MatchResult.match() : MatchResult.notMatch());
    }
}
