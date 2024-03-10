package tgc.plus.authservice.exceptions.exceptions_elements;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Getter
public class RefreshTokenException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public RefreshTokenException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public RefreshTokenException(String message) {
        super(message);
    }
}
