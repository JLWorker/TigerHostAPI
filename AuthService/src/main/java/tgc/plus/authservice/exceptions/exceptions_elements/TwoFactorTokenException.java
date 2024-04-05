package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TwoFactorTokenException extends RuntimeException{
    public TwoFactorTokenException(String message) {
        super(message);
    }
}

