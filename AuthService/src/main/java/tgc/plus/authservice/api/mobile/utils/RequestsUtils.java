package tgc.plus.authservice.api.mobile.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

@Component
public class RequestsUtils {

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

    public Mono<String> getIpAddress(ServerWebExchange serverWebExchange){

        if (serverWebExchange.getAttributes().isEmpty()){
            return Mono.just("0.0.0.0");
        }
        
        HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
        String ipAddressProxy = Arrays.stream(headersList).map(headers::getFirst)
                .filter(el -> el != null && !el.isEmpty() && !"unknown".equalsIgnoreCase(el))
                .map(h -> h.split(",")[0])
                .reduce("", (h1, h2) -> h2 + ":" + h1);

        if (ipAddressProxy.isEmpty()) {
            String ipAddressDevice = Objects.requireNonNull(serverWebExchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
            if (ipAddressDevice.contains("0:0:0:0:0:0:0:1"))
                return Mono.just("127.0.0.1");
            else
                return Mono.just(Objects.requireNonNull(serverWebExchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
        }
        else
            return Mono.just(ipAddressProxy.split(":")[0]);

    }

}
