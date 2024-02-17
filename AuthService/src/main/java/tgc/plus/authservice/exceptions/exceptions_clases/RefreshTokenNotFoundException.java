package tgc.plus.authservice.exceptions.exceptions_clases;

public class RefreshTokenNotFoundException extends RuntimeException{
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
