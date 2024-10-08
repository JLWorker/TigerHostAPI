package tgc.plus.authservice.exceptions.exceptions_elements;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TwoFactorActiveException extends RuntimeException{

    private String deviceToken;

    public TwoFactorActiveException(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
