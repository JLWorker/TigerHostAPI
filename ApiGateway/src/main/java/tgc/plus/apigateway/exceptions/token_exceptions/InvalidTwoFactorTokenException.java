package tgc.plus.apigateway.exceptions.token_exceptions;

public class InvalidTwoFactorTokenException extends RuntimeException{
    public InvalidTwoFactorTokenException() {
        super("Token invalid");
    }
}
