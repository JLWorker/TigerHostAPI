package tgc.plus.authservice.facades.utils.factories;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.kafka_message_dto.FeedbackMessage;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;
import tgc.plus.authservice.dto.kafka_message_dto.MailMessage;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;
import tgc.plus.authservice.exceptions.exceptions_elements.CommandNotFoundException;
import tgc.plus.authservice.facades.utils.MainFacadeUtils;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;

@Component
public class KafkaMessageFactory {

    public Mono<KafkaMessage> createKafkaMessage(KafkaMessageType type, String identifyElem, Payload payload){
        return switch (type){
            case MAIL_MESSAGE -> Mono.just(new MailMessage(identifyElem, payload));
            case FEEDBACK_MESSAGE -> Mono.just(new FeedbackMessage(payload, identifyElem));
            default -> Mono.error(new CommandNotFoundException(String.format("Elem with name %s not found", type)));
        };
    }


}
