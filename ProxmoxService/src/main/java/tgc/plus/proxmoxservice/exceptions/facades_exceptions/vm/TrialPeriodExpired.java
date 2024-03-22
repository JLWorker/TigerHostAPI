package tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class TrialPeriodExpired extends RuntimeException{

    public TrialPeriodExpired(String message) {
        super(message);
    }
}
