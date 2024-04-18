package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;

@Getter
public class MailMessage extends KafkaMessage {

    @JsonProperty("user_code")
    private String userCode;

    public MailMessage(String userCode, Payload payload) {
        super(payload);
        this.userCode = userCode;
    }
}
