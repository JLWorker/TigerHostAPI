//package tgc.plus.apigateway.filters;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.io.buffer.DefaultDataBufferFactory;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
//import org.springframework.web.bind.support.DefaultDataBinderFactory;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//import reactor.netty.http.server.HttpServerResponse;
//
//@RequiredArgsConstructor
//public class AuthCookieCreatorFilter implements GatewayFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpResponse response = exchange.getResponse();
//        ServerHttpResponseDecorator decorator = new ServerHttpResponseDecorator(response);
//        DefaultDataBufferFactory factory = new DefaultDataBufferFactory().join().read
//        exchange.getResponse().bufferFactory()
//    }
//}
