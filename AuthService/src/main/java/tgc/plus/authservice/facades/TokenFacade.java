package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.exceptions.exceptions_clases.InvalidRequestException;
import tgc.plus.authservice.exceptions.exceptions_clases.RefreshTokenNotFoundException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

@Service
@Slf4j
public class TokenFacade {


    //update поченить
    //проверить операции
    //починить callService

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Transactional(noRollbackForClassName = "RefreshTokenException")
    public Mono<UpdateTokenResponse> updateAccessToken(UpdateToken updateToken){
        return facadeUtils.getRefreshToken(updateToken.getRefreshToken())
                .flatMap(userToken -> tokenService.checkRefreshToken(userToken.getExpiredDate())
                        .flatMap(result -> facadeUtils.updatePairTokens(userToken.getRefreshToken(), userToken.getUserId(), result)
                                .flatMap(tokenPair -> Mono.just(new UpdateTokenResponse(tokenPair)))))
                .doOnError(e->log.error(e.getMessage()));
    }

    @Transactional //?
    public Mono<Void> deleteToken(String tokenId) {
        return facadeUtils.removeUserToken(tokenId)
                .doOnError(e -> log.error(e.getMessage())); //вернуть информацию
    }

    @Transactional
    public Mono<Void> deleteAllToken(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext ->
                facadeUtils.removeAllUserTokens(tokenId, securityContext.getAuthentication().getPrincipal().toString()))
                .doOnError(e -> log.error(e.getMessage())); //вернуть информацию
    }



}
