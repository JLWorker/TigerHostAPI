package tgc.plus.authservice.exceptions.exceptions_clases;

public class AccessTokenExpiredException extends TokenException {

    public AccessTokenExpiredException(String message) {
        super(message);
    }
}
