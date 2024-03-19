package tgc.plus.proxmoxservice.configs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.exceptions_dto.ResponseException;
import tgc.plus.proxmoxservice.exceptions.authorize_exceptions.AccessTokenExpiredException;

@Slf4j
public class JwtAuthenticationHandler implements ServerAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

        if (exception instanceof AccessTokenExpiredException)
          return generateAnswer(true, response, request, exception);

        else
            return generateAnswer(false, response, request, exception);
    }

    private Mono<Void> generateAnswer(Boolean expired, ServerHttpResponse response, ServerHttpRequest request, AuthenticationException exception){
        return Mono.defer(()->{
            if (expired) {
                response.getHeaders().add("Expired", "true");
            } else
                response.getHeaders().add("Logout", "true");

            response.getHeaders().add("Content-Type", "application/json");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);

            ResponseException responseException = new ResponseException(request.getPath().toString(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), exception.getMessage());

            try {
                return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(responseException))));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).doOnError(e -> log.error(e.getMessage()));
    }

}
