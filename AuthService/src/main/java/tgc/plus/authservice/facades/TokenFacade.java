package tgc.plus.authservice.facades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.ParallelAccessTokenUpdateException;
import tgc.plus.authservice.facades.utils.annotations.IpValid;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaimsDto;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDto;
import tgc.plus.authservice.dto.tokens_dto.*;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.RefreshTokenException;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.CookiePayload;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.db_client_repository.CustomDatabaseClientRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.CookieService;
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
    private final FacadeUtils facadeUtils;
    private final KafkaMessageFactory kafkaMessageFactory;
    private final TokenMetaRepository tokenMetaRepository;
    private final CustomDatabaseClientRepository customDatabaseClientRepository;
    private final CookieService cookieService;
    private final UserRepository userRepository;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    //проверить на параллельность обновления
    @Transactional
    public Mono<UpdateTokenResponseDto> updateAccessToken(String cookieData, UpdateTokenDto updateTokenDto, ServerHttpResponse serverHttpResponse){
        return userTokenRepository.getBlockForUserTokenByTokenId(updateTokenDto.getTokenId())
                .zipWith(cookieService.getRefreshFromCookie(cookieData))
                .filter(result -> result.getT1().getId()!=null)
                .switchIfEmpty(facadeUtils.getRefreshTokenException())
                .filter(result -> result.getT2().equals(result.getT1().getRefreshToken()))
                .switchIfEmpty(Mono.error(new ParallelAccessTokenUpdateException("Access token already update")))
                .flatMap(result -> userRepository.getUserById(result.getT1().getUserId())
                        .flatMap(user -> tokenService.updatePairTokens(new AccessTokenClaimsDto(user.getUserCode(), user.getRole()), result.getT2(), result.getT1().getExpiredDate()))
                        .flatMap(tokens -> cookieService.addRefCookie(serverHttpResponse, tokens.getT2())
                                .thenReturn(new UpdateTokenResponseDto(tokens.getT1())))
                )
                .onErrorResume(e->{
                     if (!(e instanceof RefreshTokenException) && !(e instanceof ParallelAccessTokenUpdateException)){
                        log.error(e.getMessage());
                        return facadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }


    @Transactional
    public Mono<Void> logoutToken(String cookieData, ServerHttpResponse serverHttpResponse){
        return facadeUtils.getPrincipal()
                .flatMap(userCode -> cookieService.getRefreshFromCookie(cookieData)
                        .flatMap(refreshToken -> userTokenRepository.getBlockForUserTokenByRefreshToken(refreshToken)
                        .filter(Objects::nonNull)
                        .switchIfEmpty(facadeUtils.getRefreshTokenException())
                        .then(userTokenRepository.deleteUserTokenByRefreshToken(refreshToken)
                                .doFinally(res -> serverHttpResponse.addCookie(ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name()).maxAge(0).build()))))
                        .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null))
                        .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDto(userCode), PartitioningStrategy.BASIC_MESSAGES))
                )
                .onErrorResume(e->{
                    if (!(e instanceof RefreshTokenException)){
                        log.error(e.getMessage());
                        return facadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> deleteToken(String tokenId) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return userTokenRepository.getBlockForUserTokenByTokenId(tokenId)
                    .filter(Objects::nonNull)
                    .switchIfEmpty(facadeUtils.getNotFoundException("Refresh token already removed"))
                    .then(userTokenRepository.removeUserTokenByTokenId(tokenId))
                    .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                                    .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDto(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                    .onErrorResume(e -> {
                        if(!(e instanceof NotFoundException)){
                            log.error(e.getMessage());
                            return facadeUtils.getServerException();
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
                    .switchIfEmpty(facadeUtils.getRefreshTokenException())
                    .flatMap(list -> userTokenRepository.deleteUserTokensExceptTokenId(tokenId, list.get(0).getUserId()))
                    .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                            .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDto(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                    .onErrorResume(e -> {
                         if (!(e instanceof RefreshTokenException)){
                            log.error(e.getMessage());
                            return facadeUtils.getServerException();
                        }
                        else
                            return Mono.error(e);
                    });
        });
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Mono<List<TokenDataDto>> getAllTokens(String tokenId, @IpValid String ipAddress){
        return customDatabaseClientRepository.getBlockTokenAndMeta(tokenId)
                        .filter(Objects::nonNull)
                        .switchIfEmpty(facadeUtils.getRefreshTokenException())
                        .flatMap(result -> result.getDeviceIp().equals(ipAddress) ? Mono.just(result) :
                                tokenMetaRepository.updateIpAddress(ipAddress, result.getUserTokenId()).thenReturn(result))
                        .flatMap(result -> customDatabaseClientRepository.getAllUserTokens(result.getUserId()))
                        .onErrorResume(e -> {
                            if (e instanceof PessimisticLockingFailureException){
                                return facadeUtils.getRefreshTokenException();
                            }
                            else if (!(e instanceof RefreshTokenException)) {
                                log.error(e.getMessage());
                                return facadeUtils.getServerException();
                            }
                            else
                                return Mono.error(e);
                        });
    }

}
