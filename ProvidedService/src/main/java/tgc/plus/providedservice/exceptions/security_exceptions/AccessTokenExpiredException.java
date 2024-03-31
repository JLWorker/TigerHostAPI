package tgc.plus.providedservice.exceptions.security_exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {

    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
