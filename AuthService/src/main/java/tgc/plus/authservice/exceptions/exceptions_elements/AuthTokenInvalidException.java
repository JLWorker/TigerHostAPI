package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenInvalidException extends AuthenticationException {

    public AuthTokenInvalidException(String message) {
        super(message);
    }
}
