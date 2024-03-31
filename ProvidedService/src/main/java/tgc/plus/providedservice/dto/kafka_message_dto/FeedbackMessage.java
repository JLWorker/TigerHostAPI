package tgc.plus.providedservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.providedservice.dto.kafka_message_dto.message_payloads.Payload;

@Getter
public class FeedbackMessage {

    @JsonProperty("type")
    private String operationType;

    @JsonProperty()
    private Payload payload;

    public FeedbackMessage(Payload payload, String operationType) {
        this.operationType = operationType;
    }

}
