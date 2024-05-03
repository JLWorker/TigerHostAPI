package tgc.plus.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import tgc.plus.apigateway.config.utils.ServicesUriList;
import tgc.plus.apigateway.filters.IpAddressProxyGatewayFilter;
import tgc.plus.apigateway.filters.JwtGatewayFilter;
import tgc.plus.apigateway.filters.RefreshCookieFilter;
import tgc.plus.apigateway.filters.TwoFactorGatewayFilter;

@Configuration
@RequiredArgsConstructor
public class ApiGatewayConfig {

    private final IpAddressProxyGatewayFilter ipAddressProxyGatewayFilter;
    private final JwtGatewayFilter jwtGatewayFilter;
    private final TwoFactorGatewayFilter twoFactorGatewayFilter;
    private final RefreshCookieFilter refreshCookieFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route(r -> r.path("/api/auth/2fa/verify-code")
                        .filters(f -> {
                            f.filter(ipAddressProxyGatewayFilter);
                            f.filter(twoFactorGatewayFilter);
                            return f;
                        })
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r -> r.path("/api/auth/tokens/update")
                        .filters(f -> f.filter(refreshCookieFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r -> r.path( "/api/auth/tokens/logout")
                        .filters(f -> {
                            f.filter(jwtGatewayFilter);
                            f.filter(refreshCookieFilter);
                            return f;
                        })
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))


                .route(r -> r.path("/api/auth/account/login", "/api/auth/tokens/all/*")
                        .and()
                        .method(HttpMethod.POST, HttpMethod.GET)
                        .filters(f->f.filter(ipAddressProxyGatewayFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r -> r.path("/api/auth/account/info", "/api/auth/account/password", "/api/account/email", "/api/account/phone")
                        .and()
                        .method(HttpMethod.PATCH, HttpMethod.GET)
                        .filters(f->f.filter(jwtGatewayFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))
                .route(r->r.path("/api/auth/tokens/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.PATCH, HttpMethod.DELETE)
                        .filters(f -> f.filter(jwtGatewayFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))
                .route(r->r.path("/api/auth/2fa/**")
                        .and()
                        .method(HttpMethod.GET, HttpMethod.PATCH)
                        .filters(f->f.filter(jwtGatewayFilter))
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r->r.path("/api/auth/account/**", "/api/auth/2fa/**", "/api/auth/tokens/**", "/api/auth/admin/**")
                        .uri(ServicesUriList.AUTH_SERVICE.getUrl()))

                .route(r->r.path("/api/feedback/**")
                        .and()
                        .method(HttpMethod.GET)
                        .filters(f->f.filter(jwtGatewayFilter))
                        .uri(ServicesUriList.FEEDBACK_SERVICE.getUrl())
                )

                .route(r->r.path("/api/vm/**")
                        .filters(f->f.filter(jwtGatewayFilter))
                        .uri(ServicesUriList.PROXMOX_SERVICE.getUrl()))

                .route(r->r.path("/api/provided/**")
                .filters(f->f.filter(jwtGatewayFilter))
                .uri(ServicesUriList.PROVIDED_SERVICE.getUrl()))
                .build();

    }

    @Bean
    public WebProperties.Resources handlerResources(){
        return new WebProperties.Resources();
    }

}
