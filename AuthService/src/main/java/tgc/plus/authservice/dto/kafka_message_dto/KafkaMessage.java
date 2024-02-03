package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KafkaMessage {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("payload")
    private Payload payload;

    public KafkaMessage(String userCode, Payload payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
