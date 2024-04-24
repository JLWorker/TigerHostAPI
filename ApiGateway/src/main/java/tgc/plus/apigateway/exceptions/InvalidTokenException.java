package tgc.plus.apigateway.exceptions;

import lombok.Getter;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
