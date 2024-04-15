package tgc.plus.providedservice.configs;

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
import tgc.plus.providedservice.configs.utils.*;
import tgc.plus.providedservice.services.TokenService;

@Configuration
@EnableWebFluxSecurity
public class SpringSecurityConfig {

    @Autowired
    private TokenService tokenService;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authorizeExchange(exchange ->
                        exchange
//                                .pathMatchers("/api/provided/admin/**").hasAnyRole(RoleList.ADMIN.name())
                                .pathMatchers("/api/provided/**").permitAll()
                                .pathMatchers("/v3/**", "/webjars/**", "/swagger-ui.html").permitAll()
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedMethod("GET, POST, PATCH");
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }

}
