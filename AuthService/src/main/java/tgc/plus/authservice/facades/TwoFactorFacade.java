package tgc.plus.authservice.facades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDTO;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorCode;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.TwoFactorCodeException;
import tgc.plus.authservice.exceptions.exceptions_elements.TwoFactorTokenException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TwoFactorRepository;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.TwoFactorService;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwoFactorFacade {

    private final TokenService tokenService;
    private final TwoFactorService twoFactorService;
    private final FacadeUtils facadeUtils;
    private final UserRepository userRepository;
    private final KafkaMessageFactory kafkaMessageFactory;
    private final TwoFactorRepository twoFactorRepository;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    @Transactional
    public Mono<Void> switch2Fa(){
        return facadeUtils.getPrincipal()
                .flatMap(userCode -> userRepository.getBlockForUserByUserCode(userCode)
                .flatMap(user -> {
                    if(user.getTwoAuthStatus())
                        return userRepository.switchTwoFactorStatus(user.getUserCode(), false);
                    else
                        return  (user.getTwoFactorSecret()!=null) ? userRepository.switchTwoFactorStatus(user.getUserCode(), true) :
                          twoFactorService.generateSecret()
                                  .flatMap(secret -> userRepository.updateTwoFactorSecret(user.getUserCode(), secret));
                })
                .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_ACCOUNT.getName(), null))
                .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
        }

    @Transactional(readOnly = true)
    public Mono<QrCodeData> getQrCode(){
        return facadeUtils.getPrincipal()
                .flatMap(userRepository::getUserByUserCodeAndSecret)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadeUtils.getNotFoundException("Secret code not found for user, turn on two factor status"))
                .flatMap(user -> twoFactorService.generateQrCode(user.getEmail(), user.getTwoFactorSecret()))
                .onErrorResume(e -> {
                    if (!(e instanceof NotFoundException)) {
                        log.error(e.getMessage());
                        return facadeUtils.getServerException();
                    } else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<TokensResponse> verifyCode(TwoFactorCode twoFactorCode, String token, String ipAddress, ServerHttpResponse serverHttpResponse){
        return tokenService.get2FaTokenData(token)
                .flatMap(deviceToken -> userRepository.getUserByTwoFactorDeviceToken(deviceToken)
                    .filter(Objects::nonNull)
                    .switchIfEmpty(tokenService.get2FaTokenException())
                    .flatMap(user -> twoFactorService.verify(twoFactorCode.getCode(), user.getTwoFactorSecret())
                        .filter(res -> res)
                        .switchIfEmpty(Mono.error(new TwoFactorCodeException("Authentication code is incorrect")))
                        .then(twoFactorRepository.removeTwoFactorByDeviceToken(deviceToken))
                        .then(facadeUtils.generatePairTokens(user, twoFactorCode.getDeviceData(), ipAddress)
                                        .flatMap(tokens -> facadeUtils.addRefCookie(serverHttpResponse, tokens.getT2())
                                        .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                                                .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(user.getUserCode()), PartitioningStrategy.BASIC_MESSAGES)))
                                        .thenReturn(new TokensResponse(tokens.getT1(), tokens.getT2().getTokenId()))))))
                .onErrorResume(e -> {
                    if(!(e instanceof TwoFactorTokenException) && !(e instanceof TwoFactorCodeException)){
                        log.error(e.getMessage());
                        return facadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

}