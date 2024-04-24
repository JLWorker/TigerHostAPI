package tgc.plus.authservice.configs.utils;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.services.TokenService;

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
