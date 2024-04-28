package tgc.plus.authservice.facades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.admin_dto.ChangeAccountDto;
import tgc.plus.authservice.dto.admin_dto.GetUsersByDateDto;
import tgc.plus.authservice.dto.admin_dto.UserResponseDto;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDto;
import tgc.plus.authservice.dto.kafka_message_dto.headers.MailHeadersDto;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditEmailPayloadDto;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.NotFoundException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.facades.utils.annotations.EmailValid;
import tgc.plus.authservice.facades.utils.annotations.UserCodeValid;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.utils.utils_enums.RoleElem;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminFacade {

    private final FacadeUtils facadeUtils;
    private final UserRepository userRepository;
    private final KafkaMessageFactory kafkaMessageFactory;
    private final UserTokenRepository userTokenRepository;

    @Value("${kafka.topic.mail-service}")
    private String mailTopic;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    public Mono<List<String>> getSystemRoles(){
        return Mono.defer(() -> Mono.just(Arrays.stream(RoleElem.values()).map(Enum::name).collect(Collectors.toList())));
    }

    @Transactional
    public Mono<Void> changeAccount(ChangeAccountDto changeAccountDTO){
        return userRepository.getBlockForUserByUserCode(changeAccountDTO.getUserCode())
                .filter(Objects::nonNull)
                .switchIfEmpty(facadeUtils.getNotFoundException(String.format("User with user code - %s, not found!", changeAccountDTO.getUserCode())))
                .flatMap(user -> {
                    user.setEmail(changeAccountDTO.getEmail());
                    user.setActive(changeAccountDTO.getActive());
                    user.setTwoAuthStatus(changeAccountDTO.getTwoAuthStatus());
                    user.setRole(changeAccountDTO.getRole());
                    return Mono.just(user);
                })
                .flatMap(userRepository::save)
                .flatMap(updatedUser -> {
                    if (updatedUser.getEmail().equals(changeAccountDTO.getEmail()))
                        return kafkaMessageFactory.createKafkaMessage(KafkaMessageType.MAIL_MESSAGE, updatedUser.getUserCode(), new EditEmailPayloadDto(updatedUser.getEmail()))
                                .flatMap(mailMessage-> facadeUtils.sendMessageInKafkaTopic(mailMessage, mailTopic, new MailHeadersDto(MailServiceCommand.UPDATE_EMAIL.getName()), PartitioningStrategy.MESSAGES_MAKING_CHANGES))
                                .thenReturn(updatedUser);
                    else
                        return  Mono.just(updatedUser);
                })
                .flatMap(updatedUser -> kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_ACCOUNT.getName(), null)
                        .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDto(updatedUser.getUserCode()), PartitioningStrategy.BASIC_MESSAGES)))
                .onErrorResume(e -> {
                    if (!(e instanceof NotFoundException)){
                        log.error(e.getMessage());
                        return facadeUtils.getServerException();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> closeAccountSessions(@UserCodeValid String userCode){
        return userTokenRepository.getBlockForAllUserTokens(userCode)
                .collectList()
                .filter(userTokens -> !userTokens.isEmpty())
                .switchIfEmpty(Mono.empty())
                .flatMap(userTokens -> userTokenRepository.deleteAllByUserId(userTokens.get(0).getUserId()))
                .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                        .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDto(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
    }

    @Transactional(readOnly = true)
    public Mono<List<UserResponseDto>> getUsersByDateAndLimit(GetUsersByDateDto getUsersByDateDTO){
        Instant startDate = LocalDate.parse(getUsersByDateDTO.getStartDate()).atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC);
        Instant finishDate = LocalDate.parse(getUsersByDateDTO.getFinishDate()).atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
        return userRepository.getAllByDateTimeAndCount(startDate, finishDate, getUsersByDateDTO.getLimit())
                .map(UserResponseDto::new)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
    }

    @Transactional(readOnly = true)
    public Mono<UserResponseDto> getUserByEmail(@EmailValid String email){
        return userRepository.getUserByEmail(email)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadeUtils.getNotFoundException(String.format("User with email %s not found", email)))
                .map(UserResponseDto::new)
                .onErrorResume(e -> {
                if(!(e instanceof NotFoundException)) {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                }
                else
                    return Mono.error(e);
                });
    }

}
