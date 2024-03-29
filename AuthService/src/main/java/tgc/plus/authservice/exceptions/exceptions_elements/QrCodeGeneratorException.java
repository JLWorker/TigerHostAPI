package tgc.plus.authservice.exceptions.exceptions_elements;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class QrCodeGeneratorException extends RuntimeException{
    public QrCodeGeneratorException(String message) {
        super(message);
    }
}
