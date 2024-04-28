package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.PayloadDto;

@Getter
public class FeedbackMessageDto extends KafkaMessageDto {

    @JsonProperty("type")
    private String operationType;

    public FeedbackMessageDto(PayloadDto payloadDto, String operationType) {
        super(payloadDto);
        this.operationType = operationType;
    }

}
