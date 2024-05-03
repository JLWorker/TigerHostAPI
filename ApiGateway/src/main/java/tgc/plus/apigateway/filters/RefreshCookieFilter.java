package tgc.plus.apigateway.filters;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.exceptions.cookie_exceptions.InvalidCookieException;
import tgc.plus.apigateway.exceptions.cookie_exceptions.MissingCookieException;
import tgc.plus.apigateway.exceptions.service_exceptions.ServerException;
import tgc.plus.apigateway.filters.utils.utils_enums.CookiePayload;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.util.Base64;

@Component
@Slf4j
public class RefreshCookieFilter implements GatewayFilter, Ordered {

    @Value("${cookie.hmac}")
    private String cookieHmac;
    private Mac mac;

    @PostConstruct
    @SneakyThrows
    private void init(){
        mac = Mac.getInstance("HmacSHA256");
        SecretKey cookieKey = Keys.hmacShaKeyFor(cookieHmac.getBytes());
        mac.init(cookieKey);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpCookie httpCookie = exchange.getRequest().getCookies().getFirst(CookiePayload.REFRESH_TOKEN.name());
        if (httpCookie == null){
            return Mono.error(new MissingCookieException(String.format("Required cookie '%s' is not present", CookiePayload.REFRESH_TOKEN.name())));
        }
        else {
            try {
                String cookiePayload = httpCookie.getValue();
                String[] parts = cookiePayload.split("\\.");
                Mac cloneMac = (Mac) mac.clone();
                byte[] payloadMac = Base64.getEncoder().encode(cloneMac.doFinal(parts[0].getBytes()));
                byte[] expectedMac = parts[1].getBytes();
                if (!MessageDigest.isEqual(payloadMac, expectedMac)) {
                    return Mono.error(new InvalidCookieException("Invalid cookie"));
                }
            }
            catch (Exception e){
                if (!(e instanceof InvalidCookieException)){
                    log.error(e.getMessage());
                    return Mono.error(new ServerException("The server is currently unable to complete the request"));
                }

            }
        }
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {
        return 1;
    }
}
