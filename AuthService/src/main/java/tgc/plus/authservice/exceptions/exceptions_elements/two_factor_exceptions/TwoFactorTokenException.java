package tgc.plus.authservice.exceptions.exceptions_elements.two_factor_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TwoFactorTokenException extends RuntimeException{

    public TwoFactorTokenException(String message) {
        super(message);
    }
}
