package tgc.plus.authservice.exceptions.exceptions_clases;

public class TwoFactorActive extends RuntimeException{
    public TwoFactorActive(String message) {
        super(message);
    }
}
