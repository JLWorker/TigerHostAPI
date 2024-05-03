package tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ParallelAccessTokenUpdateException extends RuntimeException{
    public ParallelAccessTokenUpdateException(String message) {
        super(message);
    }
}
