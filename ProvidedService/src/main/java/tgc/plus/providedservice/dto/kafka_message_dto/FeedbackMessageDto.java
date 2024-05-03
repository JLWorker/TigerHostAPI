package tgc.plus.providedservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.providedservice.dto.kafka_message_dto.message_payloads.PayloadDto;

@Getter
public class FeedbackMessageDto {

    @JsonProperty("type")
    private String operationType;

    @JsonProperty()
    private PayloadDto payloadDto;

    public FeedbackMessageDto(PayloadDto payloadDto, String operationType) {
        this.operationType = operationType;
    }

}
