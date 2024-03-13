package tgc.plus.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import tgc.plus.apigateway.config.utils.ServicesUriList;
import tgc.plus.apigateway.filters.IpAddressProxyFilter;
import tgc.plus.apigateway.filters.JwtGatewayFilterFactory;

@Configuration
public class ApiGatewayConfig {

    @Autowired
    IpAddressProxyFilter ipAddressProxyFilter;

    @Autowired
    JwtGatewayFilterFactory jwtGatewayFilterFactory;

    //add filters for cookies

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()

                .route(r -> r.path("/api/account/login", "/api/2fa/verify-code", "/api/tokens/all")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.GET)
                        .filters(f->f.filter(ipAddressProxyFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r -> r.path("/api/account/info", "/api/account/password", "/api/account/email", "/api/account/phone")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.GET)
                        .filters(f->f.filter(ipAddressProxyFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))
                .route(r->r.path("/api/tokens/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.DELETE)
                        .filters(f -> f.filter(jwtGatewayFilterFactory))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))
                .route(r->r.path("/api/2fa/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.PATCH)
                        .filters(f->f.filter(jwtGatewayFilterFactory))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r->r.path("/api/account/**", "/api/2fa/**", "/api/tokens/**")
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r->r.path("/api/feedback/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f->f.filter(jwtGatewayFilterFactory))
                        .uri(ServicesUriList.FEEDBACK_SERVICE.getUrl()))
                .build();

    }

}
