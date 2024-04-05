package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TwoFactorCodeException extends RuntimeException{

    public TwoFactorCodeException(String message) {
        super(message);
    }
}
