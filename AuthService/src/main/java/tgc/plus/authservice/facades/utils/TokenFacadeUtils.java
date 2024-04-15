package tgc.plus.authservice.facades.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.RefreshTokenException;
import tgc.plus.authservice.exceptions.exceptions_elements.ServerException;
import tgc.plus.authservice.repository.UserTokenRepository;

import java.util.Objects;

@Component
public class TokenFacadeUtils {


    public <T> Mono<T> getRefreshTokenException(){
        return Mono.error(new RefreshTokenException("Refresh token not exist"));
    }

    public <T> Mono<T> getNotFoundException(String message){
        return Mono.error(new NotFoundException(message));
    }

    public <T> Mono<T> getServerException(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }




}
