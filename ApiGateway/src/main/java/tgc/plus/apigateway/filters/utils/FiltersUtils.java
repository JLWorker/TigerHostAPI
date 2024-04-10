package tgc.plus.apigateway.filters.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Header;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.ResponseException;

@Component
public class FiltersUtils {

    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    public Mono<ServerWebExchange> getErrorResponse(ServerWebExchange serverWebExchange, Exception e){
        ServerHttpResponse response = serverWebExchange.getResponse();
        DataBufferFactory factory = serverWebExchange.getResponse().bufferFactory();
        ResponseException responseException = new ResponseException(serverWebExchange.getRequest().getPath().value(),
                HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        byte[] responseBody = objectMapper.writeValueAsBytes(responseException);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return serverWebExchange.getResponse()
                .writeWith(Mono.just(factory.wrap(responseBody)))
                .thenReturn(serverWebExchange);
    }

}
