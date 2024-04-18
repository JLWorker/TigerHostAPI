package tgc.plus.authservice.api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.authservice.dto.exceptions_dto.ExceptionResponse;
import tgc.plus.authservice.dto.exceptions_dto.VersionExceptionResponse;
import tgc.plus.authservice.exceptions.exceptions_elements.*;

import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionResponse> validHandlerException(WebExchangeBindException exception, ServerWebExchange request) {
            BindingResult bindingResult = exception.getBindingResult();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
    }
    @ExceptionHandler({TwoFactorActiveException.class, RefreshTokenException.class})
    public ServerHttpResponse runtimeHandlerException(Exception exception, ServerWebExchange exchange) {
        if (exception instanceof TwoFactorActiveException) {
            exchange.getResponse().getHeaders().add("2FA-Token", ((TwoFactorActiveException) exception).getDeviceToken());
        } else {
            exchange.getResponse().getHeaders().add("Logout", "true");
        }
        return exchange.getResponse();
//                ExceptionResponse response = new ExceptionResponse(request.getPath().value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Logout", "true").body(response);

    }

    @ExceptionHandler(VersionException.class)
    public ResponseEntity<VersionExceptionResponse> versionHandlerException(VersionException exception, ServerWebExchange request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new VersionExceptionResponse(request.getRequest().getPath().toString(),
                HttpStatus.CONFLICT.getReasonPhrase(),  HttpStatus.CONFLICT.value(), exception.getMessage(), exception.getNewVersion()));
    }

}
