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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import tgc.plus.authservice.configs.utils.*;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfig {

    @Autowired
    UserService userService;

    @Autowired
    TokenService tokenService;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/account/reg", "/account/login", "/account/recovery", "/account/create", "/account/check",
                                        "/tokens/update").permitAll()
                                .pathMatchers("/account/phone", "/account/email", "/tokens/token", "/tokens/tokenAll").authenticated()
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
        return new JwtReactiveAuthenticationManager(userService);
    }

    @Bean
    public ReactiveAuthenticationManager reactiveLoginAuthenticationManager(){
        return new LoginReactiveAuthenticationManager(userService, bCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(reactiveFilterAuthenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(new JwtServerAuthenticationConverter(tokenService));
        authenticationWebFilter.setRequiresAuthenticationMatcher(new JwtServerWebExchangeMatcher());
        authenticationWebFilter.setAuthenticationFailureHandler(new JwtAuthenticationHandler(new ObjectMapper()));
        return authenticationWebFilter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedOrigin("http://localhost:5173");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

}
