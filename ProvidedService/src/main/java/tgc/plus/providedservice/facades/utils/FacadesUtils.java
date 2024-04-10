package tgc.plus.providedservice.facades.utils;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import tgc.plus.providedservice.configs.KafkaProducerConfig;
import tgc.plus.providedservice.dto.kafka_message_dto.FeedbackMessage;
import tgc.plus.providedservice.entities.ProvidedServiceEntity;
import tgc.plus.providedservice.exceptions.facade_exceptions.InvalidRequestException;
import tgc.plus.providedservice.exceptions.facade_exceptions.RelatedElementsException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ServerException;
import tgc.plus.providedservice.repositories.TariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
public class FacadesUtils {

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    @Autowired
    private TariffRepository tariffRepository;

    public <T> Mono<Boolean> getBlockForElements(Integer elemId, Class<T> classType){
        String table = classType.getAnnotation(Table.class).value();
        return customDatabaseRepository.getBlockForElement(elemId, table)
                .filter(Objects::nonNull)
                .switchIfEmpty(getResourceNotFoundException(String.format("Element with id - %s not found", elemId)));
    }

    public <T> Mono<T> getCallableBlockForElements(Integer elemId, Class<T> classType){
        return customDatabaseRepository.getCallableBlockForElement(elemId, classType)
                .filter(Objects::nonNull)
                .switchIfEmpty(getResourceNotFoundException(String.format("Element with id - %s not found", elemId)));
    }


    public Mono<Void> sendMessageInFeedbackService(EventTypesList eventType, MessageAvailableList headerType){
        FeedbackMessage newMessage = new FeedbackMessage(null, eventType.getCommand());
        ProducerRecord<String, FeedbackMessage> producerRecord = new ProducerRecord<>(feedbackTopic, newMessage);
        producerRecord.headers().add("user_code", headerType.getKey().getBytes());
        SenderRecord<String, FeedbackMessage, String> senderRecord = SenderRecord.create(producerRecord, UUID.randomUUID().toString());
        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord)).then();
    }

    public <T extends ProvidedServiceEntity> Mono<Void> changeElement(Integer elemId, Class<T> targetClass, EventTypesList eventType, Function<T, T> changeParams){
        return getCallableBlockForElements(elemId, targetClass)
                .map(changeParams)
                .flatMap(elem -> tariffRepository.updateElem(elem)
                        .onErrorResume(e ->{
                            if (e instanceof DataIntegrityViolationException) {
                                return getInvalidRequestException(String.format("Element with parameter - %s already exist", elem.getUniqueElement()));
                            }
                            return Mono.error(e);
                        }))
                .then(sendMessageInFeedbackService(eventType, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if(!(e instanceof ResourceNotFoundException) && !(e instanceof InvalidRequestException)){
                        log.error(e.getMessage());
                        return getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }



    public  <T> Mono<T> getServerError(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }

    public  <T> Mono<T> getResourceNotFoundException(String message){
        return Mono.error(new ResourceNotFoundException(message));
    }

    public  <T> Mono<T> getRelatedElementsException(String message){
        return Mono.error(new RelatedElementsException(message));
    }

    public <T> Mono<T> getInvalidRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

}
