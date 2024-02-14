package tgc.plus.authservice.exceptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.authservice.dto.exceptions_dto.ResponseException;
import tgc.plus.authservice.dto.exceptions_dto.VersionResponseException;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;

import java.util.Objects;

@org.springframework.web.bind.annotation.ControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ResponseException> validHandlerException(WebExchangeBindException exception, ServerWebExchange request) {
            BindingResult bindingResult = exception.getBindingResult();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseException(request.getRequest().getPath().toString(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseException> runtimeHandlerException(RuntimeException exception, ServerWebExchange request) {

            if (exception instanceof AuthenticationException){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseException(request.getRequest().getPath().toString(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
            }
            else if (exception instanceof KafkaException) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ResponseException(request.getRequest().getPath().toString(),
                    HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),  HttpStatus.SERVICE_UNAVAILABLE.value(), "Kafka broker is faulty, sending messages is impossible"));
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseException(request.getRequest().getPath().toString(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(VersionException.class)
    public ResponseEntity<VersionResponseException> versionHandlerException(VersionException exception, ServerWebExchange request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new VersionResponseException(request.getRequest().getPath().toString(),
                HttpStatus.CONFLICT.getReasonPhrase(),  HttpStatus.CONFLICT.value(), exception.getMessage(), exception.getNewVersion()));
    }

}
