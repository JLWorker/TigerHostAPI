package tgc.plus.authservice.exceptions;

public abstract class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
