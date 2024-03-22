package tgc.plus.authservice.exceptions;
import dev.samstevens.totp.exceptions.QrGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.KafkaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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
    @ExceptionHandler({RuntimeException.class, QrGenerationException.class})
    public ResponseEntity<ExceptionResponse> runtimeHandlerException(Exception exception, ServerWebExchange request) {

            if (exception instanceof TwoFactorActiveException)
                    return ResponseEntity.status(HttpStatus.ACCEPTED)
                            .header("2FA-Token", ((TwoFactorActiveException) exception).getDeviceToken()).build();


            else if (exception instanceof QrGenerationException) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),  HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
            }

            else if (exception instanceof RefreshTokenException) {

                HttpStatus httpStatus = ((RefreshTokenException) exception).getHttpStatus();
                ExceptionResponse response = new ExceptionResponse(request.getRequest().getPath().toString(), httpStatus.getReasonPhrase(), httpStatus.value(), exception.getMessage());

                if (httpStatus.equals(HttpStatus.UNAUTHORIZED))
                    return ResponseEntity.status(httpStatus).header("Logout", "true").body(response);
                else
                    return ResponseEntity.status(httpStatus).body(response);

            }
            else if (exception instanceof NotFoundException){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(), HttpStatus.NOT_FOUND.value(), exception.getMessage()));
            }

            else if (exception instanceof AuthenticationException || exception instanceof TwoFactorTokenException){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value(), exception.getMessage()));
            }
            else if (exception instanceof KafkaException) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                    HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),  HttpStatus.SERVICE_UNAVAILABLE.value(), "Kafka broker is faulty, sending messages is impossible"));
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponse(request.getRequest().getPath().toString(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), exception.getMessage()));
    }

    @ExceptionHandler(VersionException.class)
    public ResponseEntity<VersionExceptionResponse> versionHandlerException(VersionException exception, ServerWebExchange request){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new VersionExceptionResponse(request.getRequest().getPath().toString(),
                HttpStatus.CONFLICT.getReasonPhrase(),  HttpStatus.CONFLICT.value(), exception.getMessage(), exception.getNewVersion()));
    }

}
