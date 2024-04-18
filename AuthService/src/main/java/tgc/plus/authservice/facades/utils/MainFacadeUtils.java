package tgc.plus.authservice.facades.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaims;
import tgc.plus.authservice.dto.kafka_message_dto.MailMessage;
import tgc.plus.authservice.dto.kafka_message_dto.FeedbackMessage;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;
import tgc.plus.authservice.dto.kafka_message_dto.headers.KafkaHeadersDTO;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditEmailData;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.EditPhoneData;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.SaveUserData;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.dto.user_dto.UserChangeContacts;
import tgc.plus.authservice.entities.TokenMeta;
import tgc.plus.authservice.entities.User;
import tgc.plus.authservice.exceptions.exceptions_elements.*;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.utils.TokenExpiredDate;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class MainFacadeUtils {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenMetaRepository tokenMetaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Mono<Void> checkPasswords(String password, String confirmPassword){
       return (password.equals(confirmPassword)) ? Mono.empty() : getInvalidRequestException("Passwords mismatch");
    }


//    public Mono<MailMessage> createMessageForRestorePsw(String token, String userCode){
//        return Mono.defer(()->{
//            PasswordRestoreData passwordRestoreData = new PasswordRestoreData(recoverUrl+token);
//            return Mono.just(new MailMessage(userCode, passwordRestoreData));
//        });
//    }

    public Mono<MailMessage> createMessageForSaveUser(String userCode, String email, String password){
        return Mono.defer(()->{
            SaveUserData saveUserData = new SaveUserData(email, password);
            return Mono.just(new MailMessage(userCode, saveUserData));
        });
    }

    public Mono<FeedbackMessage> createMessageForUpdateUserInfo(String eventType){
        return Mono.defer(() -> Mono.just(new FeedbackMessage(null, eventType)));
    }

    public Mono<MailMessage> createMessageForContactUpdate(String userCode, String contact, String type){
        return Mono.defer(()->{
            if (type.equals(UserChangeContacts.ChangeEmail.class.getName())){
                EditEmailData editEmailData = new EditEmailData(contact);
                return Mono.just(new MailMessage(userCode, editEmailData));
            }
            else {
                EditPhoneData editPhoneData = new EditPhoneData(contact);
                return Mono.just(new MailMessage(userCode, editPhoneData));
            }
        });
    }

//    public Mono<Void> sendMessageInFeedbackService(FeedbackMessage feedbackMessage, String userCode){
//        ProducerRecord<String, KafkaMessage> messageRecord = new ProducerRecord<>(feedbackTopic, "other", feedbackMessage);
//        messageRecord.headers().add("user_code", userCode.getBytes());
//        SenderRecord<String, KafkaMessage, String> senderRecord = SenderRecord.create(messageRecord, UUID.randomUUID().toString());
//        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord))
//                .onErrorResume(e -> Mono.error(new KafkaException(e.getMessage())))
//                .then(Mono.empty());
//    }

    public Mono<String> getPrincipal(){
        return Mono.defer(() -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal().toString()));
    }

    public <T> Mono<T> createKafkaMessage(String identElem, Payload payload, Class<T> classType){
        return Mono.defer(() -> {
            try {
                return Mono.just(classType.getDeclaredConstructor(identElem.getClass(), payload.getClass())
                        .newInstance(identElem, payload));
            }
            catch (Exception e) {
                return Mono.error(new RuntimeException(e.getMessage()));
            }
        });
    }

    public Mono<Void> sendMessageInKafkaTopic(KafkaMessage message, String topic, KafkaHeadersDTO kafkaHeadersDTO, PartitioningStrategy strategy){
        ProducerRecord<String, KafkaMessage> messageRecord = new ProducerRecord<>(topic, strategy.getStrategy(), message);
        Map<String, String> headers = objectMapper.convertValue(kafkaHeadersDTO, new TypeReference<>() {});
        headers.forEach((key, value) -> messageRecord.headers().add(key, value.getBytes()));
        SenderRecord<String, KafkaMessage, String> senderRecord = SenderRecord.create(messageRecord, UUID.randomUUID().toString());
        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord)).then();
    }

        public Mono<TokensResponse> generatePairTokens(User user, DeviceData deviceData, String ipAddr){
        return tokenService.createAccessToken(new AccessTokenClaims(user.getUserCode(), user.getRole()), TokenExpiredDate.SECURITY.getName())
                .zipWith(tokenService.createRefToken(user.getId()))
                .flatMap(tokens -> tokenMetaRepository.save(new TokenMeta(tokens.getT2().getId(), ipAddr, deviceData.getName(), deviceData.getApplicationType()))
                        .flatMap(tokenMeta -> {
                            log.info(String.format("User with email - %s, logged, tokens was created", user.getEmail() ));
                            return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(), tokens.getT2().getTokenId()));
                        }));
    }


    public <T> Mono<T> getRefreshTokenException(){
        return Mono.error(new RefreshTokenException("Refresh token not exist"));
    }

    public <T> Mono<T> getNotFoundException(String message){
        return Mono.error(new NotFoundException(message));
    }

    public <T> Mono<T> getServerException(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }

    public <T> Mono<T> getInvalidRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

}
