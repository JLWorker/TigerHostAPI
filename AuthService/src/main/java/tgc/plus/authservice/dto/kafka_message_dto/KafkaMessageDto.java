package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.PayloadDto;

public abstract class KafkaMessageDto {
    @JsonProperty
    private PayloadDto payloadDto;

    public KafkaMessageDto(PayloadDto payloadDto) {
        this.payloadDto = payloadDto;
    }
}
