package tgc.plus.authservice.facades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.facades.utils.annotations.IpValid;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaims;
import tgc.plus.authservice.dto.kafka_message_dto.FeedbackMessage;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDTO;
import tgc.plus.authservice.dto.tokens_dto.*;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.RefreshTokenException;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.MainFacadeUtils;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.repository.db_client_repository.CustomDatabaseClientRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class TokenFacade {

    private final TokenService tokenService;
    private final UserTokenRepository userTokenRepository;
    private final MainFacadeUtils mainFacadeUtils;
    private final KafkaMessageFactory kafkaMessageFactory;
    private final TokenMetaRepository tokenMetaRepository;
    private final CustomDatabaseClientRepository customDatabaseClientRepository;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    @Transactional
    public Mono<UpdateTokenResponse> updateAccessToken(UpdateToken updateToken){
        return customDatabaseClientRepository.getInfoForTokensUpdate(updateToken.getRefreshToken())
                .filter(Objects::nonNull)
                .switchIfEmpty(mainFacadeUtils.getRefreshTokenException())
                .flatMap(data -> tokenService.updatePairTokens(new AccessTokenClaims(data.getUserCode(), data.getRole()), updateToken.getRefreshToken(),  data.getExpiredDate()))
                .onErrorResume(e->{
                     if (!(e instanceof RefreshTokenException)){
                        log.error(e.getMessage());
                        return mainFacadeUtils.getServerException();
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
                    .switchIfEmpty(mainFacadeUtils.getNotFoundException("Refresh token already removed"))
                    .then(userTokenRepository.removeUserTokenByTokenId(tokenId))
                    .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                                    .flatMap(kafkaMessage -> mainFacadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                    .onErrorResume(e -> {
                        if(!(e instanceof NotFoundException)){
                            log.error(e.getMessage());
                            return mainFacadeUtils.getServerException();
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
                    .switchIfEmpty(mainFacadeUtils.getRefreshTokenException())
                    .flatMap(list -> userTokenRepository.deleteUserTokensExceptTokenId(tokenId, list.get(0).getUserId()))
                    .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                            .flatMap(kafkaMessage -> mainFacadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                    .onErrorResume(e -> {
                         if (!(e instanceof RefreshTokenException)){
                            log.error(e.getMessage());
                            return mainFacadeUtils.getServerException();
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
                        .switchIfEmpty(mainFacadeUtils.getRefreshTokenException())
                        .flatMap(result -> result.getDeviceIp().equals(ipAddress) ? Mono.just(result) :
                                tokenMetaRepository.updateIpAddress(ipAddress, result.getUserTokenId()).thenReturn(result))
                        .flatMap(result -> customDatabaseClientRepository.getAllUserTokens(result.getUserId()))
                        .onErrorResume(e -> {
                            if (e instanceof PessimisticLockingFailureException){
                                return mainFacadeUtils.getRefreshTokenException();
                            }
                            else if (!(e instanceof RefreshTokenException)) {
                                log.error(e.getMessage());
                                return mainFacadeUtils.getServerException();
                            }
                            else
                                return Mono.error(e);
                        });
    }

}
