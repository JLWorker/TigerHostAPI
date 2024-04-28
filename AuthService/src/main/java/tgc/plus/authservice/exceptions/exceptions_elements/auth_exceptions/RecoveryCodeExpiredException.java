package tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecoveryCodeExpiredException extends RuntimeException{

    public RecoveryCodeExpiredException(String message) {
        super(message);
    }
}
