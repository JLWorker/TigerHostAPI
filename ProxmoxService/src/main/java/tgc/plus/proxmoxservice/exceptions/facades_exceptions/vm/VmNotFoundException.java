package tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VmNotFoundException extends RuntimeException{

    public VmNotFoundException(String message) {
        super(message);
    }
}
