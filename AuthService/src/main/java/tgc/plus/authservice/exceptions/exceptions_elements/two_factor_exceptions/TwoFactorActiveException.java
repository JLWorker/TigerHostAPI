package tgc.plus.authservice.exceptions.exceptions_elements.two_factor_exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.ACCEPTED)
public class TwoFactorActiveException extends RuntimeException{

    private final String deviceToken;

    public TwoFactorActiveException(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
