package tgc.plus.authservice.configs.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.UserService;

@Slf4j
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final UserRepository userRepository;

    public JwtReactiveAuthenticationManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.defer(()-> {
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
            return ReactiveSecurityContextHolder.getContext().flatMap(securityContext ->
                    userRepository.getUserByUserCode(authenticationToken.getPrincipal().toString())
                            .filter(user -> user!=null && securityContext.getAuthentication()==null)
                            .switchIfEmpty(Mono.error(new AuthenticationException("Token already exist or user not exist")))
                            .flatMap(user -> {
                                securityContext.setAuthentication(authenticationToken);
                                return Mono.just(securityContext.getAuthentication());
                            }));
        }
        else
            return raiseBadCredentials();
    }
    private <T> Mono<T> raiseBadCredentials() {
        return Mono.error(new BadCredentialsException("Invalid Credentials"));
    }
}