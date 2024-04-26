package tgc.plus.apigateway.exceptions.cookie_exceptions;

public class MissingCookieException extends RuntimeException{
    public MissingCookieException(String message) {
        super(message);
    }
}
