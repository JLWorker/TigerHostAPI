package tgc.plus.apigateway.exceptions;

public class AccessTokenCorruptionException extends RuntimeException {
    public AccessTokenCorruptionException(String msg) {
        super(msg);
    }
}
