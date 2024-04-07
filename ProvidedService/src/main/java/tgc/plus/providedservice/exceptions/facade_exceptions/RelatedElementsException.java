package tgc.plus.providedservice.exceptions.facade_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RelatedElementsException extends RuntimeException{
    public RelatedElementsException(String message) {
        super(message);
    }
}
