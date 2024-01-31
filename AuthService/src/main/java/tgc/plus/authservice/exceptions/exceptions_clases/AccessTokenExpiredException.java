package tgc.plus.authservice.exceptions.exceptions_clases;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {

    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
