package tgc.plus.authservice.configs.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.services.UserService;

@Slf4j
public class LoginReactiveAuthenticationManager implements ReactiveAuthenticationManager {
    private final UserService userService;
    public LoginReactiveAuthenticationManager(UserService userService) {
        this.userService = userService;
    }

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                        .cast(UsernamePasswordAuthenticationToken.class)
                        .flatMap(this::authenticateToken)
                        .filter(element -> bCryptPasswordEncoder.matches((String) authentication.getCredentials(), element.getPassword()))
                        .switchIfEmpty(raiseBadCredentials())
                        .flatMap(userDetails -> ReactiveSecurityContextHolder.getContext()
                                .flatMap(securityContext -> {
                                    securityContext.setAuthentication(new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities()));
                                    return Mono.just(securityContext.getAuthentication());
                                }));
    }

    public Mono<UserDetails> authenticateToken(UsernamePasswordAuthenticationToken authenticationToken) {
        if(authenticationToken != null) {
            return userService.findByUsername(authenticationToken.getPrincipal().toString())
                    .switchIfEmpty(raiseBadCredentials())
                    .doOnError(e -> log.error(e.getMessage()));
        }
        else {
            return raiseBadCredentials();
        }
    }
    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Invalid Credentials"));
    }
}
