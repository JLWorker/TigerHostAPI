package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenExpiredException extends AuthenticationException {
    public AuthTokenExpiredException(String message) {
        super(message);
    }
}
