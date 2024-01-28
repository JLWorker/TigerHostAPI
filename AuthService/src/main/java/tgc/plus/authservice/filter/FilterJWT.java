package tgc.plus.authservice.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.exceptions.exceptions_clases.AccessDataTokenException;
import tgc.plus.authservice.exceptions.exceptions_clases.UserAuthenticateException;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

@Component
public class FilterJWT implements WebFilter {

    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer_")){

            return tokenService.getUserCode(token.substring(8)).flatMap(userCode ->
                    userService.authenticateUser(userCode).flatMap(result -> {
                        if (result !=null)
                            SecurityContextHolder.getContext().setAuthentication(result);
                        else
                            return Mono.error(new UserAuthenticateException("Authentication failed"));
                        return chain.filter(exchange);
                    }));
        }
        else
            return Mono.error(new AccessDataTokenException("Access token damage"));
    }
}
