package tgc.plus.feedbackgateaway.exceptions.exception_elements;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenExpiredException extends AuthenticationException {

    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
