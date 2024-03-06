package tgc.plus.feedbackgateaway.configs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       return Mono.defer(()->
                 Mono.just(authentication)
                        .cast(UsernamePasswordAuthenticationToken.class)
                        .flatMap(this::authenticateToken)
        ).doOnError(e->log.error(e.getMessage()));
    }

    public Mono<Authentication> authenticateToken(UsernamePasswordAuthenticationToken authenticationToken) {
        if(authenticationToken != null) {
            return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
                if (securityContext.getAuthentication() == null){
                    securityContext.setAuthentication(authenticationToken);
                    return Mono.just(securityContext.getAuthentication());
                }
                else
                    return Mono.error(new AuthenticationException("Token already authenticate"));
            });
        }
        else
            return raiseBadCredentials();
    }
    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Invalid Credentials"));
    }
}