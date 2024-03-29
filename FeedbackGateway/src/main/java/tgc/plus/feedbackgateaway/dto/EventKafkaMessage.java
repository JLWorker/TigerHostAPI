package tgc.plus.feedbackgateaway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.feedbackgateaway.dto.message_payloads.Payload;

@NoArgsConstructor
@Getter
@Setter
public class EventKafkaMessage {

    @JsonProperty("type")
    String operationType;

    @JsonProperty
    Payload payload;

    public EventKafkaMessage(String operationType, Payload payload) {
        this.operationType = operationType;
        this.payload = payload;
    }

    public EventKafkaMessage(String operationType) {
        this.operationType = operationType;
    }
}
