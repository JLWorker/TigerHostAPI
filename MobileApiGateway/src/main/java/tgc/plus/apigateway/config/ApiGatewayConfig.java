package tgc.plus.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.apigateway.filters.IpAddressProxyFilter;
import tgc.plus.apigateway.filters.JwtGatewayFilterFactory;

@Configuration
public class ApiGatewayConfig {

    @Autowired
    IpAddressProxyFilter ipAddressProxyFilter;

    @Autowired
    JwtGatewayFilterFactory jwtGatewayFilterFactory;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r->r.path("/mobile-api/**")
                        .filters(f->f.stripPrefix(1))
                        .uri("http://localhost:8080"))

                .route(r -> r.path("/account/login", "/2fa/verify-code", "/tokens/all")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.GET)
                        .filters(f->f.filter(ipAddressProxyFilter))
                        .uri("http://localhost:8081"))

                .route(r -> r.path("/account/info", "/account/password", "/account/email", "/account/phone")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.GET)
                        .filters(f->f.filter(ipAddressProxyFilter))
                        .uri("http://localhost:8081"))
                .route(r->r.path("/tokens/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.DELETE)
                        .filters(f -> f.filter(jwtGatewayFilterFactory))
                        .uri("http://localhost:8081"))
                .route(r->r.path("/2fa/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.PATCH)
                        .filters(f->f.filter(jwtGatewayFilterFactory))
                        .uri("http://localhost:8081"))
                .route(r->r.path("/account/**", "/2fa/**", "/tokens/**")
                        .uri("http://localhost:8081"))

                .route(r->r.path("/feedback/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f->f.filter(jwtGatewayFilterFactory))
                        .uri("http://localhost:8083"))
                .build();

    }

}
