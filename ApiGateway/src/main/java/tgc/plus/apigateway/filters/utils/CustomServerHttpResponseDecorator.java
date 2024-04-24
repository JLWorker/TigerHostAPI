package tgc.plus.apigateway.filters.utils;

import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;

public class CustomServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    public CustomServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }



}
