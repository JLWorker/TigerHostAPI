package tgc.plus.apigateway.exceptions.token_exceptions;

public class InvalidAccessTokenException extends RuntimeException {

    public InvalidAccessTokenException() {
        super("Token invalid");
    }
}
