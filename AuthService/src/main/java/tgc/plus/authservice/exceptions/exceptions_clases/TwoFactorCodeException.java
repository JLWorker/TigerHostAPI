package tgc.plus.authservice.exceptions.exceptions_clases;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.Map;


public class TwoFactorCodeException extends RuntimeException{

    public TwoFactorCodeException(String message) {
        super(message);
    }
}
