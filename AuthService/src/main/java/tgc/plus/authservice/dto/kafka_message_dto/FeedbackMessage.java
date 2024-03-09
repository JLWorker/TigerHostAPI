package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;

@Getter
public class FeedbackMessage extends KafkaMessage {

    @JsonProperty("type")
    private String operationType;

    public FeedbackMessage(Payload payload, String operationType) {
        super(payload);
        this.operationType = operationType;
    }

}
