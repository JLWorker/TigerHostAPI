package tgc.plus.authservice.facades;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.admin_dto.ChangeAccountDTO;
import tgc.plus.authservice.dto.admin_dto.GetUsersByDateDTO;
import tgc.plus.authservice.dto.admin_dto.UserResponseDTO;
import tgc.plus.authservice.dto.kafka_message_dto.headers.FeedbackHeadersDTO;
import tgc.plus.authservice.dto.kafka_message_dto.headers.MailHeadersDTO;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditEmailData;
import tgc.plus.authservice.exceptions.exceptions_elements.NotFoundException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.facades.utils.factories.KafkaMessageFactory;
import tgc.plus.authservice.facades.utils.utils_enums.FeedbackEventType;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.utils.utils_enums.RoleElem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
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
    public Mono<Void> changeAccount(ChangeAccountDTO changeAccountDTO){
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
                .flatMap(updatedUser -> (updatedUser.getEmail().equals(changeAccountDTO.getEmail())) ? Mono.just(updatedUser) :
                        kafkaMessageFactory.createKafkaMessage(KafkaMessageType.MAIL_MESSAGE, updatedUser.getUserCode(), new EditEmailData(updatedUser.getEmail()))
                                .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, mailTopic, new MailHeadersDTO(MailServiceCommand.UPDATE_EMAIL.name()), PartitioningStrategy.MESSAGES_MAKING_CHANGES))
                                .thenReturn(updatedUser))
                .flatMap(updatedUser -> kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_ACCOUNT.getName(), null)
                        .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(updatedUser.getUserCode()), PartitioningStrategy.BASIC_MESSAGES)))
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
    public Mono<Void> closeAccountSessions(String userCode){
        return userTokenRepository.getBlockForAllUserTokens(userCode)
                .collectList()
                .filter(userTokens -> !userTokens.isEmpty())
                .switchIfEmpty(Mono.empty())
                .flatMap(userTokens -> userTokenRepository.deleteAllByUserId(userTokens.get(0).getUserId()))
                .then(kafkaMessageFactory.createKafkaMessage(KafkaMessageType.FEEDBACK_MESSAGE, FeedbackEventType.UPDATE_TOKENS.getName(), null)
                        .flatMap(kafkaMessage -> facadeUtils.sendMessageInKafkaTopic(kafkaMessage, feedbackTopic, new FeedbackHeadersDTO(userCode), PartitioningStrategy.BASIC_MESSAGES)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
    }

    @Transactional(readOnly = true)
    public Mono<List<UserResponseDTO>> getUsersByDateAndLimit(GetUsersByDateDTO getUsersByDateDTO){
        if (getUsersByDateDTO.getLimit()<=0){
            getUsersByDateDTO.setLimit(-1);
        }
        LocalDateTime startDate = LocalDateTime.parse(getUsersByDateDTO.getStartDate());
        LocalDateTime finishDate = LocalDateTime.parse(getUsersByDateDTO.getFinishDate()).plusDays(1).minusSeconds(1);
        return userRepository.getAllByDateTimeAndCount(Instant.from(startDate), Instant.from(finishDate), getUsersByDateDTO.getLimit())
                .map(UserResponseDTO::new)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
    }

    @Transactional(readOnly = true)
    public Mono<UserResponseDTO> getUserByEmail(String email){
        return userRepository.getUserByEmail(email)
                .map(UserResponseDTO::new)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadeUtils.getServerException();
                });
    }

}
