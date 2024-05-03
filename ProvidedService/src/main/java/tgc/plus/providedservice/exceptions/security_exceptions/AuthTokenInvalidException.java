package tgc.plus.providedservice.exceptions.security_exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenInvalidException extends AuthenticationException {

    public AuthTokenInvalidException(String message) {
        super(message);
    }
}
