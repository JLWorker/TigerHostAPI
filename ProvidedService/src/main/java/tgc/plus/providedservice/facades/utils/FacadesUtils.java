package tgc.plus.providedservice.facades.utils;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import tgc.plus.providedservice.configs.KafkaProducerConfig;
import tgc.plus.providedservice.dto.kafka_message_dto.FeedbackMessage;
import tgc.plus.providedservice.entities.VdsTariff;
import tgc.plus.providedservice.exceptions.facade_exceptions.InvalidRequestException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ServerException;
import tgc.plus.providedservice.repositories.VdsTariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.Objects;
import java.util.UUID;

@Component
public class FacadesUtils {

    @Autowired
    private VdsTariffRepository vdsTariffRepository;

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Value("${kafka.topic.feedback-service}")
    private String feedbackTopic;

    public <T> Mono<Void> getBlockForElements(Integer tariffId, Class<T> classType){
        String table = classType.getAnnotation(Table.class).value();
        return customDatabaseRepository.getBlockForElement(tariffId, table)
                .filter(res -> res!=0)
                .switchIfEmpty(getResourceNotFoundException(String.format("Element with id - %s not found", tariffId)))
                .then();
    }


    public Mono<Void> sendMessageInFeedbackService(EventTypesList eventType, MessageAvailableList headerType){
        FeedbackMessage newMessage = new FeedbackMessage(null, eventType.getCommand());
        ProducerRecord<String, FeedbackMessage> producerRecord = new ProducerRecord<>(feedbackTopic, newMessage);
        producerRecord.headers().add("user_code", headerType.getKey().getBytes());
        SenderRecord<String, FeedbackMessage, String> senderRecord = SenderRecord.create(producerRecord, UUID.randomUUID().toString());
        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord)).then();
    }


    public  <T> Mono<T> getServerError(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }

    public  <T> Mono<T> getResourceNotFoundException(String message){
        return Mono.error(new ResourceNotFoundException(message));
    }

    public <T> Mono<T> getInvalidRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

}
