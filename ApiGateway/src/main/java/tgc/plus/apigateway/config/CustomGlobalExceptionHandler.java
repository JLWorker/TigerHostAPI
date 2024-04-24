package tgc.plus.apigateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;
import tgc.plus.apigateway.config.utils.TokenResponseHeader;
import tgc.plus.apigateway.dto.error_responses_dto.ResponseException;
import tgc.plus.apigateway.exceptions.ExpiredTokenException;

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
        return route(RequestPredicates.path("/api/2fa/verify-code"), this::twoFactorTokenExceptionHandler)
                .andRoute(RequestPredicates.all(), this::accessTokenExceptionHandler);
    }

    private Mono<ServerResponse> accessTokenExceptionHandler(ServerRequest request){
            Throwable throwable = getError(request);
            HttpHeaders headers = new HttpHeaders();
            if (throwable instanceof ExpiredTokenException)
                headers.add(TokenResponseHeader.EXPIRED.getName(), "true");
            else
                headers.add(TokenResponseHeader.LOGOUT.getName(), "true");
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(new ResponseException(request.path(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), throwable.getMessage()));
            }

    private Mono<ServerResponse> twoFactorTokenExceptionHandler(ServerRequest request){
        Throwable throwable = getError(request);
        return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ResponseException(request.path(), throwable.getClass().toString(), HttpStatus.UNAUTHORIZED.value(), throwable.getMessage()));
    }
}





