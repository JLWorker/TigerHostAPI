package tgc.plus.providedservice.configs.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.exceptions_dto.auth_exceptions.ExceptionResponseDto;
import tgc.plus.providedservice.exceptions.security_exceptions.AuthTokenExpiredException;
import tgc.plus.providedservice.exceptions.security_exceptions.AuthTokenInvalidException;
import tgc.plus.providedservice.facades.utils.CookiePayload;


import java.rmi.ServerException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationHandler implements ServerAuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    @Order
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        ServerHttpRequest request = webFilterExchange.getExchange().getRequest();

        if (exception instanceof AuthTokenExpiredException) {
            response.getHeaders().add(TokenResponseHeader.EXPIRED.getName(), "true");
        }
        else if (exception instanceof AuthTokenInvalidException){
            response.addCookie(ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name()).maxAge(0).build());
            response.getHeaders().add(TokenResponseHeader.LOGOUT.getName(), "true");
        }
        response.getHeaders().add("Content-Type", "application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        ExceptionResponseDto exceptionResponseDto = new ExceptionResponseDto(request.getPath().value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        try {
            return response.writeWith(Mono.just(response.bufferFactory().wrap(objectMapper.writeValueAsBytes(exceptionResponseDto))));
        } catch (JsonProcessingException e) {
                log.error(e.getMessage());
                return Mono.error(new ServerException("The server is currently unable to complete the request"));
        }
    }

}
