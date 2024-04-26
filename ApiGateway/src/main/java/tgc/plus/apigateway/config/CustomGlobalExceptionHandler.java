package tgc.plus.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.config.utils.TokenResponseHeader;
import tgc.plus.apigateway.dto.error_responses_dto.ResponseException;
import tgc.plus.apigateway.exceptions.cookie_exceptions.InvalidCookieException;
import tgc.plus.apigateway.exceptions.cookie_exceptions.MissingCookieException;
import tgc.plus.apigateway.exceptions.token_exceptions.ExpiredAccessTokenException;
import tgc.plus.apigateway.exceptions.token_exceptions.InvalidAccessTokenException;
import tgc.plus.apigateway.filters.utils.utils_enums.CookiePayload;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
@Slf4j
@Order(-2)
public class CustomGlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    public CustomGlobalExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer codecConfigurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(codecConfigurer.getWriters());
    }

    
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return route(RequestPredicates.all(), this::globalGatewayHandler);
    }

    private Mono<ServerResponse> globalGatewayHandler(ServerRequest request){
        Throwable throwable = getError(request);
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        if(throwable instanceof ExpiredAccessTokenException)
             headers.add(TokenResponseHeader.EXPIRED.getName(), "true");
        else if ((throwable instanceof InvalidAccessTokenException )|| (throwable instanceof InvalidCookieException))
            headers.add(TokenResponseHeader.LOGOUT.getName(), "true");
        else if (throwable instanceof MissingCookieException)
            status = HttpStatus.BAD_REQUEST;

        ResponseException exception = new ResponseException(request.path(), status.getReasonPhrase(), status.value(), throwable.getMessage());
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(exception)
                .flatMap(serverResponse -> {
                    if (throwable instanceof InvalidCookieException) {
                        ResponseCookie responseCookie = ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name()).maxAge(0).build();
                        return ServerResponse.from(serverResponse).cookie(responseCookie).bodyValue(exception);
                    }
                    return Mono.just(serverResponse);
                });
    }

}





