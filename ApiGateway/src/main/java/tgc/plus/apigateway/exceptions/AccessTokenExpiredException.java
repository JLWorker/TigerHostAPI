package tgc.plus.apigateway.exceptions;

public class AccessTokenExpiredException extends RuntimeException {

    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
