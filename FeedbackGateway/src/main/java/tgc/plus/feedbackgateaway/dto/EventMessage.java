package tgc.plus.feedbackgateaway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.feedbackgateaway.dto.message_payloads.Payload;

import java.time.Duration;

@NoArgsConstructor
@Getter
public class EventMessage {

    @JsonProperty("type")
    String operationType;

    @JsonProperty
    Payload payload;

    public EventMessage(String operationType, Payload payload) {
        this.operationType = operationType;
        this.payload = payload;
    }

    public EventMessage(String operationType) {
        this.operationType = operationType;
    }
}
