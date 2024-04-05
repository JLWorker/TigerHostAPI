package tgc.plus.feedbackgateaway.configs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.feedbackgateaway.services.TokenService;

@Component
@Slf4j
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter{

    private final TokenService tokenService;

    public JwtServerAuthenticationConverter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return tokenService.getTokenFromRequest(exchange)
                .flatMap(tokenService::getAuthentication)
                .doOnError(e -> log.error(e.getMessage()));
        }
}
