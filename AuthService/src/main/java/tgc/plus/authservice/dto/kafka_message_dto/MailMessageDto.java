package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.PayloadDto;

@Getter
public class MailMessageDto extends KafkaMessageDto {

    @JsonProperty("user_code")
    private String userCode;

    public MailMessageDto(String userCode, PayloadDto payloadDto) {
        super(payloadDto);
        this.userCode = userCode;
    }
}
