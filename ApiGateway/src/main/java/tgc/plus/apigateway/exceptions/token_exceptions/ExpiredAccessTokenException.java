package tgc.plus.apigateway.exceptions.token_exceptions;

public class ExpiredAccessTokenException extends RuntimeException{
    public ExpiredAccessTokenException(String message) {
        super(message);
    }
}
