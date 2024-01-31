package tgc.plus.authservice.configs.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtServerAuthenticationConverter implements ServerAuthenticationConverter{

    private final TokenProvider tokenProvider;

    public JwtServerAuthenticationConverter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return tokenProvider.getTokenFromRequest(exchange)
                .flatMap(tokenProvider::getAuthentication);
        }
}
