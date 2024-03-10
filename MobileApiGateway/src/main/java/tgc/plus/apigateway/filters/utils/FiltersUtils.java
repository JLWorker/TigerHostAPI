package tgc.plus.apigateway.filters.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.dto.ResponseException;

@Component
public class FiltersUtils {

    @Autowired
    ObjectMapper objectMapper;

    @SneakyThrows
    public Mono<ServerWebExchange> getErrorResponse(ServerWebExchange serverWebExchange, Exception e, HttpStatus httpStatus){
        DataBufferFactory factory = serverWebExchange.getResponse().bufferFactory();
        ResponseException responseException = new ResponseException(serverWebExchange.getRequest().getPath().value(),
                httpStatus.getReasonPhrase(), httpStatus.value(), e.getMessage());

        byte[] responseBody = objectMapper.writeValueAsBytes(responseException);
        return serverWebExchange.getResponse().writeWith(Mono.just(factory.wrap(responseBody)))
                .thenReturn(serverWebExchange);
    }

}
