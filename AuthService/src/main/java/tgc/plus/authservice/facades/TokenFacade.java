package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.api.mobile.utils.IpValid;
import tgc.plus.authservice.dto.tokens_dto.*;
import tgc.plus.authservice.facades.utils.EventsTypesNames;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.repository.db_client_repository.CustomDatabaseClientRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

@Service
@Slf4j
@Validated
public class TokenFacade {

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    CustomDatabaseClientRepository customDatabaseClientRepository;

    @Transactional(noRollbackForClassName = "RefreshTokenException")
    public Mono<UpdateTokenResponse> updateAccessToken(UpdateToken updateToken){
        return facadeUtils.getUserTokenByRefreshToken(updateToken.getRefreshToken())
                .flatMap(userToken -> tokenService.checkExpiredRefreshToken(userToken.getExpiredDate())
                        .flatMap(result -> facadeUtils.updatePairTokens(userToken.getRefreshToken(), userToken.getUserId(), result)
                                .flatMap(tokenPair -> Mono.just(new UpdateTokenResponse(tokenPair.get("access_token"), tokenPair.get("refresh_token"))))))
                .doOnError(e->log.error(e.getMessage()));
    }

    @Transactional
    public Mono<Void> deleteToken(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.removeUserToken(tokenId)
                .then(facadeUtils.createMessageForUpdateUserInfo(EventsTypesNames.UPDATE_TOKENS.getName())
                        .flatMap(feedbackMessage -> facadeUtils.sendMessageInFeedbackService(feedbackMessage, userCode)))
                .doOnError(e -> log.error(e.getMessage()));
        });
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Mono<Void> deleteAllTokens(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.removeAllUserTokens(tokenId)
                    .then(facadeUtils.createMessageForUpdateUserInfo(EventsTypesNames.UPDATE_TOKENS.getName())
                            .flatMap(feedbackMessage -> facadeUtils.sendMessageInFeedbackService(feedbackMessage, userCode)))
                    .doOnError(e -> log.error(e.getMessage()));
        });
    }

    @Transactional
    public Mono<TokensDataResponse> getAllTokens(String tokenId, @IpValid String ipAddress){
        return facadeUtils.getUserTokenByTokenId(tokenId, true)
                .flatMap(userToken -> facadeUtils.checkIpAddress(ipAddress, userToken.getId())
                        .flatMap(tokenMeta -> customDatabaseClientRepository.getAllUserTokens(userToken.getUserId(), tokenId)
                                .flatMap(userTokenWithMetaList ->
                                        Mono.just(new TokensDataResponse(new TokenMetaData(tokenMeta.getDeviceName(), tokenMeta.getApplicationType(), tokenMeta.getDeviceIp()),
                                                userTokenWithMetaList)))));
    }

}
