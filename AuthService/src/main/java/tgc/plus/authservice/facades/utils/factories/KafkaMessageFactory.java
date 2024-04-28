package tgc.plus.authservice.facades.utils.factories;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.kafka_message_dto.FeedbackMessageDto;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessageDto;
import tgc.plus.authservice.dto.kafka_message_dto.MailMessageDto;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.PayloadDto;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.CommandNotFoundException;
import tgc.plus.authservice.facades.utils.utils_enums.KafkaMessageType;

@Component
public class KafkaMessageFactory {

    public Mono<KafkaMessageDto> createKafkaMessage(KafkaMessageType type, String identifyElem, PayloadDto payloadDto){
        return switch (type){
            case MAIL_MESSAGE -> Mono.just(new MailMessageDto(identifyElem, payloadDto));
            case FEEDBACK_MESSAGE -> Mono.just(new FeedbackMessageDto(payloadDto, identifyElem));
            default -> Mono.error(new CommandNotFoundException(String.format("Elem with name %s not found", type)));
        };
    }


}
