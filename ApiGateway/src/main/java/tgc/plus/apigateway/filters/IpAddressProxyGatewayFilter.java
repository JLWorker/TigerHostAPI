package tgc.plus.apigateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

@Component
public class IpAddressProxyGatewayFilter implements GatewayFilter {

    private final String[] headersList = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String headerIpAddress = "Device-Ip";

        if (exchange.getAttributes().isEmpty()){
            serverHttpRequest.mutate().header(headerIpAddress, "0.0.0.0");
        }

        String ipAddressProxy = Arrays.stream(headersList).map(headers::getFirst)
                .filter(el -> el != null && !el.isEmpty() && !"unknown".equalsIgnoreCase(el))
                .map(h -> h.split(",")[0])
                .reduce("", (h1, h2) -> h2 + ":" + h1);

        if (ipAddressProxy.isEmpty()) {
            String ipAddressDevice = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
            if (ipAddressDevice.contains("0:0:0:0:0:0:0:1"))
                serverHttpRequest.mutate().header(headerIpAddress, "127.0.0.1");
            else
                serverHttpRequest.mutate().header(headerIpAddress, ipAddressDevice);
        }
        else
            serverHttpRequest.mutate().header(headerIpAddress, ipAddressProxy.split(":")[0]);

        return chain.filter(exchange);
    }


}
