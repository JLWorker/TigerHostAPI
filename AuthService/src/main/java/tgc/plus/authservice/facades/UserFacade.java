package tgc.plus.authservice.facades;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.PasswordRestoreData;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.RecoveryCodeExpiredException;
import tgc.plus.authservice.facades.utils.annotations.IpValid;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.jwt_claims_dto.TwoFactorTokenClaims;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDTO;
import tgc.plus.authservice.dto.kafka_message_dto.headers.MailHeadersDTO;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.SaveUserData;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.entities.TwoFactor;
import tgc.plus.authservice.exceptions.exceptions_elements.InvalidRequestException;
import tgc.plus.authservice.exceptions.exceptions_elements.TwoFactorActiveException;
import tgc.plus.authservice.facades.utils.*;
import tgc.plus.authservice.facades.utils.factories.ContactUserRepositoryFactory;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TwoFactorRepository;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;
import tgc.plus.authservice.services.utils.utils_enums.TokenType;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final MainFacadeUtils mainFacadeUtils;
    private final KafkaMessageFactory kafkaMessageFactory;
    private final SpringSecurityConfig springSecurityConfig;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final TwoFactorRepository twoFactorRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ContactUserRepositoryFactory contactUserRepositoryFactory;

    @Value("${kafka.topic.mail-service}")
    private String mailTopic;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    @Value("${recovery.expired_date}")
    private Integer recoveryExpiredTime;

    @Value("${recovery.tgc.web.url}")
    private String recoveryUrl;

    @Transactional
    public Mono<Void> registerUser(UserData userData) {
        return mainFacadeUtils.checkPasswords(userData.getPassword(), userData.getPasswordConfirm())
                    .then(userService.save(userData))
                    .flatMap(user -> kafkaMessageFactory.createKafkaMessage(KafkaMessageType.MAIL_MESSAGE, user.getUserCode(), new SaveUserData(userData.getEmail(), userData.getPassword())))
                            .flatMap(mailMessage -> mainFacadeUtils.sendMessageInKafkaTopic(mailMessage, mailTopic, new MailHeadersDTO(MailServiceCommand.SAVE.getName()), PartitioningStrategy.BASIC_MESSAGES))
                    .onErrorResume(e -> {
                        if (e instanceof DuplicateKeyException){
                           return mainFacadeUtils.getInvalidRequestException(String.format("User with email %s already exist", userData.getEmail()));
                        }
                        else if (!(e instanceof InvalidRequestException)){
                            log.error(e.getMessage());
                            return mainFacadeUtils.getServerException();
                        }
                        else
                            return Mono.error(e);
                    });
    }


    @Transactional(noRollbackForClassName = "TwoFactorActiveException")
    public Mono<TokensResponse> loginUser(UserLogin userLogin, @IpValid String ipAddr) {
        UserData userData = userLogin.getUserData();
        DeviceData deviceData = userLogin.getDeviceData();
        return springSecurityConfig.reactiveLoginAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(userData.getEmail(), userData.getPassword()))
                .then(userRepository.getUserByEmail(userData.getEmail()))
                .flatMap(user -> {
                    if (user.getTwoAuthStatus()) {
                        String deviceToken = UUID.randomUUID().toString();
                        TwoFactor twoFactor = new TwoFactor(user.getId(), deviceToken, Instant.now());
                        return twoFactorRepository.save(twoFactor)
                                .then(tokenService.createAccessToken(new TwoFactorTokenClaims(deviceToken), TokenType.TWO_FACTOR))
                                .flatMap(token -> Mono.error(new TwoFactorActiveException(token)));
                    } else {
                        return mainFacadeUtils.generatePairTokens(user, deviceData, ipAddr)
                                .flatMap(response -> kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                                        .flatMap(feedbackMessage -> mainFacadeUtils.sendMessageInKafkaTopic(feedbackMessage, feedbackTopic, new FeedbackHeadersDTO(user.getUserCode()), PartitioningStrategy.BASIC_MESSAGES)
                                             .thenReturn(response)));
                    }
                })
                .onErrorResume(e -> {
                    if (!(e instanceof AuthenticationException) && !(e instanceof TwoFactorActiveException)) {
                        log.error(e.getMessage());
                        return mainFacadeUtils.getServerException();
                    } else
                        return Mono.error(e);
                });

    }

    @Transactional
    public Mono<Void> changeAccountContacts(@NotNull(message = "Contact can`t be null") String newContact, MailServiceCommand command, Payload payload) {
        return mainFacadeUtils.getPrincipal()
                .flatMap(userCode -> userRepository.getBlockForUserByUserCode(userCode)
                    .then(contactUserRepositoryFactory.execSqlQueryForContact(command, newContact, userCode))
                    .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.MAIL_MESSAGE, userCode, payload)
                            .zipWith(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_ACCOUNT.getName(), null)))
                    .flatMap(messages -> mainFacadeUtils.sendMessageInKafkaTopic(messages.getT1(), mailTopic, new MailHeadersDTO(command.getName()), PartitioningStrategy.MESSAGES_MAKING_CHANGES)
                            .then(mainFacadeUtils.sendMessageInKafkaTopic(messages.getT2(), feedbackTopic, new FeedbackHeadersDTO(userCode), PartitioningStrategy.BASIC_MESSAGES))))
        .onErrorResume(e -> {
                log.error(e.getMessage());
                return mainFacadeUtils.getServerException();
        });
    }



    @Transactional
    public Mono<Void> changeAccountPassword(RestorePassword restorePassword){
        return mainFacadeUtils.getPrincipal()
                .flatMap(userCode -> mainFacadeUtils.checkPasswords(restorePassword.getPassword(), restorePassword.getPasswordConfirm())
                .then(userRepository.getBlockForUserByUserCode(userCode))
                .then(userRepository.changePassword(userCode, bCryptPasswordEncoder.encode(restorePassword.getPassword()))))
                .onErrorResume(e -> {
                    if (!(e instanceof InvalidRequestException)){
                        log.error(e.getMessage());
                        return mainFacadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> generateRecoveryCode(String email){
        return userRepository.getBlockForUserByEmail(email)
                .filter(Objects::nonNull)
                .switchIfEmpty(mainFacadeUtils.getNotFoundException(String.format("User with email %s not exist", email)))
                .flatMap(user -> {
                    String recoveryCode = UUID.randomUUID().toString();
                    Instant expiredDate = Instant.now().plus(Duration.ofHours(recoveryExpiredTime));
                    return userRepository.changeRecoveryCode(user.getUserCode(), recoveryCode, expiredDate)
                            .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.MAIL_MESSAGE, user.getUserCode(), new PasswordRestoreData(recoveryUrl+recoveryCode)))
                            .flatMap(mailMessage -> mainFacadeUtils.sendMessageInKafkaTopic(mailMessage, mailTopic, new MailHeadersDTO(MailServiceCommand.SEND_RECOVERY_CODE.getName()), PartitioningStrategy.BASIC_MESSAGES));
                })
                .onErrorResume(e -> {
                    if (!(e instanceof NotFoundException)){
                        log.error(e.getMessage());
                        return mainFacadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }


    @Transactional(noRollbackForClassName = "RecoveryCodeExpiredException")
    public Mono<Void> checkAccountRecoveryCode(String password, String passwordConfirm, String code){
        return mainFacadeUtils.checkPasswords(password, passwordConfirm)
                .then(userRepository.getBlockForUserByRecoveryCode(code))
                .filter(Objects::nonNull)
                .switchIfEmpty(mainFacadeUtils.getNotFoundException("Recovery code was used"))
                .flatMap(user -> user.getCodeExpiredDate().isBefore(Instant.now()) ? userRepository.clearRecoveryCode(user.getUserCode())
                                    .then(Mono.error(new RecoveryCodeExpiredException("Recovery code expired"))) :
                                 userRepository.changePassword(user.getUserCode(), bCryptPasswordEncoder.encode(password)))
                .onErrorResume(e -> {
                    if(!(e instanceof NotFoundException) && !(e instanceof RecoveryCodeExpiredException)){
                        log.error(e.getMessage());
                        return mainFacadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional(readOnly = true)
    public Mono<UserInfoResponse> getInfoAboutAccount(){
        return mainFacadeUtils.getPrincipal()
                .flatMap(userRepository::getUserByUserCode)
                .flatMap(user -> Mono.just(new UserInfoResponse(user.getPhone(), user.getEmail(), user.getTwoAuthStatus())))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return mainFacadeUtils.getServerException();
                });
    }


}