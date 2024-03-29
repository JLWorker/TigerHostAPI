package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeName(value = "NewVmData")
public class KafkaNewVm implements Payload{

    @JsonProperty
    private Integer template;

    @JsonProperty("os_id")
    private Integer osId;

    @JsonProperty("payment_data")
    private KafkaPaymentData kafkaPaymentData;

    @JsonProperty("tariff_data")
    private KafkaTariffData kafkaTariffData;

    @JsonProperty("vds_characters")
    private KafkaVdsCharacteristicsData kafkaVdsCharacteristicsData;

    public KafkaNewVm(Integer template, Integer osId, KafkaPaymentData kafkaPaymentData, KafkaTariffData kafkaTariffData, KafkaVdsCharacteristicsData kafkaVdsCharacteristicsData) {
        this.template = template;
        this.osId = osId;
        this.kafkaPaymentData = kafkaPaymentData;
        this.kafkaTariffData = kafkaTariffData;
        this.kafkaVdsCharacteristicsData = kafkaVdsCharacteristicsData;
    }

    @Override
    public Map<String, Object> getData() {
        return Map.of(
                "template", this.template,
                "os", this.osId,
                "payment", this.kafkaPaymentData,
                "tariff", this.kafkaTariffData,
                "characters", this.kafkaVdsCharacteristicsData
        );
    }
}
