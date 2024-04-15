package tgc.plus.authservice.exceptions.exceptions_elements;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class RefreshTokenException extends RuntimeException {

    public RefreshTokenException(String message) {
        super(message);
    }
}
