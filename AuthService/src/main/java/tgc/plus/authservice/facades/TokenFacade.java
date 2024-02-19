package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

import java.util.Map;

@Service
@Slf4j
public class TokenFacade {


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
                .doOnError(e->log.error(e.getMessage())); //вернуть новую версию всем
    }

    //execute; bad SQL grammar [UPDATE user_tokens SET refresh_token= $1, expired_date= $2, active_date= $3 WHERE refresh_token= $4]

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Mono<Void> deleteToken(String tokenId) {
        return facadeUtils.removeUserToken(tokenId)
                .doOnError(e -> log.error(e.getMessage())); //вернуть информацию
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Mono<Void> deleteAllTokens(String tokenId) {
        return facadeUtils.removeAllUserTokens(tokenId)
                .doOnError(e -> log.error(e.getMessage())); //вернуть информацию
    }



}
