package tgc.plus.authservice.exceptions.exceptions_elements;

public class RecoveryCodeExpiredException extends RuntimeException{

    public RecoveryCodeExpiredException(String message) {
        super(message);
    }
}
