package tgc.plus.authservice.api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import tgc.plus.authservice.configs.utils.TokenResponseHeader;
import tgc.plus.authservice.dto.exceptions_dto.ExceptionResponseDto;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.RefreshTokenException;
import tgc.plus.authservice.exceptions.exceptions_elements.two_factor_exceptions.TwoFactorActiveException;
import tgc.plus.authservice.facades.utils.utils_enums.CookiePayload;

import java.util.Objects;

@org.springframework.web.bind.annotation.RestControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ExceptionResponseDto> validHandlerException(WebExchangeBindException exception, ServerWebExchange request) {
            BindingResult bindingResult = exception.getBindingResult();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionResponseDto(request.getRequest().getPath().toString(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST.value(), Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage()));
    }
    @ExceptionHandler({TwoFactorActiveException.class, RefreshTokenException.class})
    public ResponseEntity<ExceptionResponseDto> runtimeHandlerException(RuntimeException exception, ServerWebExchange exchange) {
        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = exception.getClass().getAnnotation(ResponseStatus.class).value();
        if (exception instanceof TwoFactorActiveException) {
            headers.add(TokenResponseHeader.TWO_FACTOR.getName(), ((TwoFactorActiveException) exception).getDeviceToken());
            return ResponseEntity.status(status).headers(httpHeaders -> httpHeaders.addAll(headers)).build();
        } else {
            headers.add(TokenResponseHeader.LOGOUT.getName(), "true");
            exchange.getResponse().addCookie(ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name()).maxAge(0).build());
            ExceptionResponseDto response = new ExceptionResponseDto(exchange.getRequest().getPath().value(), status.getReasonPhrase(), status.value(), exception.getMessage());
            return ResponseEntity.status(status).headers(httpHeaders -> httpHeaders.addAll(headers)).body(response);
        }
    }

}
