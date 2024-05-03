package tgc.plus.feedbackgateaway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.feedbackgateaway.dto.message_payloads.PayloadDto;

@NoArgsConstructor
@Getter
@Setter
public class EventKafkaMessageDto {

    @JsonProperty("type")
    String operationType;

    @JsonProperty
    PayloadDto payloadDto;

    public EventKafkaMessageDto(String operationType, PayloadDto payloadDto) {
        this.operationType = operationType;
        this.payloadDto = payloadDto;
    }

    public EventKafkaMessageDto(String operationType) {
        this.operationType = operationType;
    }
}
