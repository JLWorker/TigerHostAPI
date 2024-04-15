package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tgc.plus.authservice.api.utils.IpValid;
import tgc.plus.authservice.dto.tokens_dto.*;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.RefreshTokenException;
import tgc.plus.authservice.facades.utils.EventsTypesNames;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.facades.utils.TokenFacadeUtils;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.repository.db_client_repository.CustomDatabaseClientRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@Slf4j
@Validated
public class TokenFacade {

    @Autowired
    private FacadeUtils facadeUtils;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserTokenRepository userTokenRepository;

    @Autowired
    private TokenFacadeUtils tokenFacadeUtils;

    @Autowired
    private TokenMetaRepository tokenMetaRepository;

    @Autowired
    private CustomDatabaseClientRepository customDatabaseClientRepository;

    @Transactional
    public Mono<UpdateTokenResponse> updateAccessToken(UpdateToken updateToken){
        return customDatabaseClientRepository.getInfoForTokensUpdate(updateToken.getRefreshToken())
                .filter(Objects::nonNull)
                .switchIfEmpty(tokenFacadeUtils.getRefreshTokenException())
                .flatMap(data -> tokenService.updatePairTokens(data.getUserCode(), data.getRole(), updateToken.getRefreshToken(),  data.getExpiredDate()))
                .onErrorResume(e->{
                     if (!(e instanceof RefreshTokenException)){
                        log.error(e.getMessage());
                        return tokenFacadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> deleteToken(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return userTokenRepository.getBlockForUserTokenById(tokenId)
                    .filter(Objects::nonNull)
                    .switchIfEmpty(tokenFacadeUtils.getNotFoundException("Refresh token already removed"))
                    .then(userTokenRepository.removeUserTokenByTokenId(tokenId))
                    .then(facadeUtils.createMessageForUpdateUserInfo(EventsTypesNames.UPDATE_TOKENS.getName())
                            .flatMap(feedbackMessage -> facadeUtils.sendMessageInFeedbackService(feedbackMessage, userCode)))
                    .onErrorResume(e -> {
                        if(!(e instanceof NotFoundException)){
                            log.error(e.getMessage());
                            return tokenFacadeUtils.getServerException();
                        }
                        else
                            return Mono.error(e);
                    });
        });
    }

    @Transactional
    public Mono<Void> deleteAllTokens(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return userTokenRepository.getBlockForAllUserTokens(userCode)
                    .collectList()
                    .filter(list -> list.stream().anyMatch(el->el.getTokenId().equals(tokenId)))
                    .switchIfEmpty(tokenFacadeUtils.getRefreshTokenException())
                    .flatMap(list -> userTokenRepository.deleteUserTokensExceptTokenId(tokenId, list.get(0).getUserId()))
                    .then(facadeUtils.createMessageForUpdateUserInfo(EventsTypesNames.UPDATE_TOKENS.getName())
                            .flatMap(feedbackMessage -> facadeUtils.sendMessageInFeedbackService(feedbackMessage, userCode)))
                    .onErrorResume(e -> {
                         if (!(e instanceof RefreshTokenException)){
                            log.error(e.getMessage());
                            return tokenFacadeUtils.getServerException();
                        }
                        else
                            return Mono.error(e);
                    });
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Mono<List<TokenData>> getAllTokens(String tokenId, @IpValid String ipAddress){
        return customDatabaseClientRepository.getBlockTokenAndMeta(tokenId)
                        .filter(Objects::nonNull)
                        .switchIfEmpty(tokenFacadeUtils.getRefreshTokenException())
                        .flatMap(result -> result.getDeviceIp().equals(ipAddress) ? Mono.just(result) :
                                tokenMetaRepository.updateIpAddress(ipAddress, result.getUserTokenId()).thenReturn(result))
                        .flatMap(result -> customDatabaseClientRepository.getAllUserTokens(result.getUserId()))
                        .onErrorResume(e -> {
                            if (e instanceof PessimisticLockingFailureException){
                                return tokenFacadeUtils.getRefreshTokenException();
                            }
                            else if (!(e instanceof RefreshTokenException)) {
                                log.error(e.getMessage());
                                return tokenFacadeUtils.getServerException();
                            }
                            else
                                return Mono.error(e);
                        });
    }

}
