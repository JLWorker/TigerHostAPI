package tgc.plus.authservice.exceptions.exceptions_clases;

public abstract class TokenException extends RuntimeException {
    public TokenException(String message) {
        super(message);
    }
}
