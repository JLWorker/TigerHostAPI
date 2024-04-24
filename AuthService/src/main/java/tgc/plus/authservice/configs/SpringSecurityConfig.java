package tgc.plus.authservice.configs;

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
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.util.PathMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPattern;
import tgc.plus.authservice.configs.utils.*;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfig {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public ServerWebExchangeMatcher permitAllServerWebMatchers(){
        return ServerWebExchangeMatchers.pathMatchers("/api/account/reg", "/api/account/login", "/api/account/recovery", "/api/account/check",
                "/api/tokens/update", "/api/2fa/verify-code", "/v3/**", "/webjars/**", "/swagger-ui.html");
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .matchers(permitAllServerWebMatchers()).permitAll()
                                .pathMatchers("/api/**").authenticated()
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
        return new JwtReactiveAuthenticationManager(userRepository);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveLoginAuthenticationManager(){
        return new LoginReactiveAuthenticationManager(userService, bCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveFilterAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter(tokenService));
        authenticationWebFilter.setRequiresAuthenticationMatcher(new JwtServerWebExchangeMatcher(permitAllServerWebMatchers()));
        authenticationWebFilter.setAuthenticationFailureHandler(new JwtAuthenticationHandler(new ObjectMapper()));
        return authenticationWebFilter;
    }

}
