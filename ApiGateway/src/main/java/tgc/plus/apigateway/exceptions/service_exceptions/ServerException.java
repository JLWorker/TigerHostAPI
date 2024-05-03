package tgc.plus.apigateway.exceptions.service_exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ServerException extends RuntimeException{

    public ServerException(String message) {
        super(message);
    }
}
