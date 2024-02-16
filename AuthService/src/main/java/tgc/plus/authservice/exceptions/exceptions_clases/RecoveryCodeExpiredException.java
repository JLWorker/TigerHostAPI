package tgc.plus.authservice.exceptions.exceptions_clases;

public class RecoveryCodeExpiredException extends RuntimeException{

    public RecoveryCodeExpiredException(String message) {
        super(message);
    }
}
