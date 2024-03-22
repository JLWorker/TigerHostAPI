package tgc.plus.proxmoxservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import tgc.plus.proxmoxservice.configs.utils.JwtAuthenticationHandler;
import tgc.plus.proxmoxservice.configs.utils.JwtReactiveAuthenticationManager;
import tgc.plus.proxmoxservice.configs.utils.JwtServerAuthenticationConverter;
import tgc.plus.proxmoxservice.configs.utils.JwtServerWebExchangeMatcher;
import tgc.plus.proxmoxservice.services.TokenService;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfig {

    @Autowired
    TokenService tokenService;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/api/vm/**").authenticated()
                                .anyExchange().denyAll()
                )
                 .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION);
        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public ReactiveAuthenticationManager reactiveFilterAuthenticationManager(){
        return new JwtReactiveAuthenticationManager();
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveFilterAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter(tokenService));
        authenticationWebFilter.setRequiresAuthenticationMatcher(new JwtServerWebExchangeMatcher());
        authenticationWebFilter.setAuthenticationFailureHandler(new JwtAuthenticationHandler(new ObjectMapper()));
        return authenticationWebFilter;
    }

}
