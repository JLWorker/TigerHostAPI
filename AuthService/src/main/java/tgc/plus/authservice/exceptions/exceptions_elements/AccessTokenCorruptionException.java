package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

public class AccessTokenCorruptionException extends AuthenticationException {
    public AccessTokenCorruptionException(String msg) {
        super(msg);
    }
}
