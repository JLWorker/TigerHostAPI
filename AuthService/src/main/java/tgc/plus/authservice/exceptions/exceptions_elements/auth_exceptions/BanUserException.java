package tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class BanUserException extends RuntimeException {
    public BanUserException(String msg) {
        super(msg);
    }
}
