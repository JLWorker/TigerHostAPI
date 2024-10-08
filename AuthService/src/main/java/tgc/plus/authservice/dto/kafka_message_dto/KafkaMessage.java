package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.Payload;

public abstract class KafkaMessage {
    @JsonProperty
    private Payload payload;

    public KafkaMessage(Payload payload) {
        this.payload = payload;
    }
}
