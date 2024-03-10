package tgc.plus.authservice.exceptions.exceptions_elements;


public class TwoFactorCodeException extends RuntimeException{

    public TwoFactorCodeException(String message) {
        super(message);
    }
}
