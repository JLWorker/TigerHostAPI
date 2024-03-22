package tgc.plus.proxmoxservice.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.ExceptionResponse;

import java.util.Objects;

@org.springframework.web.bind.annotation.RestControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ExceptionResponse>> validHandlerException(WebExchangeBindException exception, ServerHttpRequest request) {
        BindingResult bindingResult = exception.getBindingResult();
        return responseBuilder(Objects.requireNonNull(bindingResult.getFieldError()).getField(), request, HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(RuntimeException.class)
//    public Mono<ResponseEntity<ExceptionResponse>> runtimeHandlerException(RuntimeException exception, ServerHttpRequest request){
//        log.info(exception.getLocalizedMessage());
//        if (exception instanceof ServiceException)
//            return responseBuilder(exception.getMessage(), request, HttpStatus.INTERNAL_SERVER_ERROR);
//        else
//            return responseBuilder(exception.getMessage(), request, HttpStatus.NOT_FOUND);
//    }

    private Mono<ResponseEntity<ExceptionResponse>> responseBuilder(String errorMessage, ServerHttpRequest req, HttpStatus httpStatus){
        return Mono.defer(()->{
            ExceptionResponse exceptionResponse = new ExceptionResponse(req.getPath().value(), httpStatus.getReasonPhrase(), httpStatus.value(), errorMessage);
            return Mono.just(ResponseEntity.status(httpStatus.value()).body(exceptionResponse));
        });
    }


}
