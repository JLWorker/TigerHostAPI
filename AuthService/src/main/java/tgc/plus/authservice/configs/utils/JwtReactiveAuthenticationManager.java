package tgc.plus.authservice.configs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.services.UserService;

@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserService userService;
    public JwtReactiveAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
       return Mono.defer(()->{
            if (authentication.isAuthenticated())
                return Mono.just(authentication);
            else
                return Mono.just(authentication)
                        .cast(UsernamePasswordAuthenticationToken.class)
                        .flatMap(this::authenticateToken);
        }).doOnError(e->log.error(e.getMessage()));
    }

    public Mono<Authentication> authenticateToken(UsernamePasswordAuthenticationToken authenticationToken) {
        if(authenticationToken != null) {
            return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
                if (securityContext.getAuthentication() == null)
                 return userService.findByUsername(authenticationToken.getPrincipal().toString())
                        .switchIfEmpty(raiseBadCredentials())
                        .doOnError(e->log.error(e.getMessage()))
                        .flatMap(user -> {
                                securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities()));
                                return Mono.just(securityContext.getAuthentication());
                            });
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