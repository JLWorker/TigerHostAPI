package tgc.plus.authservice.exceptions;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.authservice.dto.ResponseException;
import tgc.plus.authservice.exceptions.exceptions_clases.AccessDataTokenException;
import tgc.plus.authservice.exceptions.exceptions_clases.UserAuthenticateException;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;

import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseException> validHandler(WebExchangeBindException exception, ServerWebExchange request) {
            BindingResult bindingResult = exception.getBindingResult();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseException(request.getRequest().getPath().toString(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseException> runtimeHandler(RuntimeException exception, ServerWebExchange request) {

        if (exception instanceof AccessDataTokenException) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseException(request.getRequest().getPath().toString(), HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(), exception.getMessage()));
        } else {
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseException(request.getRequest().getPath().toString(), HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), exception.getMessage()));
        }
    }

    @ExceptionHandler({VersionException.class, UserAuthenticateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ResponseException> versionException(VersionException exception, ServerWebExchange request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseException(request.getRequest().getPath().toString(), HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(), exception.getMessage(), exception.getNewVersion()));
    }

}
