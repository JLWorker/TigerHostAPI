package tgc.plus.callservice.exceptions;

import org.springframework.stereotype.Component;

public class UserAlreadyExist extends RuntimeException {
    public UserAlreadyExist(String message) {
        super(message);
    }

    public UserAlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }
}
