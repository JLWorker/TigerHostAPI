package tgc.plus.providedservice.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.providedservice.dto.exceptions_dto.auth_exceptions.ExceptionResponseDto;

import java.util.Objects;

@ControllerAdvice
public class RestControllerAdvice {
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionResponseDto> validHandlerException(WebExchangeBindException exception, ServerWebExchange request) {
        BindingResult bindingResult = exception.getBindingResult();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponseDto(request.getRequest().getPath().toString(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
    }

}
