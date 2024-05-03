package tgc.plus.feedbackgateaway.exceptions.auth_exceptions;

import org.springframework.security.core.AuthenticationException;

public class AuthTokenExpiredException extends AuthenticationException {
    public AuthTokenExpiredException(String message) {
        super(message);
    }
}
