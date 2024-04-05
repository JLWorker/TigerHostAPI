package tgc.plus.feedbackgateaway.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenCorruptionException extends AuthenticationException {
    public AccessTokenCorruptionException(String msg) {
        super(msg);
    }
}
