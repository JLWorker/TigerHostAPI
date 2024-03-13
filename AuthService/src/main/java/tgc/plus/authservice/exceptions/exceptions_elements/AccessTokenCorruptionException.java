package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.security.core.AuthenticationException;

public class AccessTokenCorruptionException extends AuthenticationException {
    public AccessTokenCorruptionException(String msg) {
        super(msg);
    }
}
