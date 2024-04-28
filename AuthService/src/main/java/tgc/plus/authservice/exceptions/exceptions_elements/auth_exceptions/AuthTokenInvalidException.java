package tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenInvalidException extends AuthenticationException {

    public AuthTokenInvalidException(String message) {
        super(message);
    }
}
