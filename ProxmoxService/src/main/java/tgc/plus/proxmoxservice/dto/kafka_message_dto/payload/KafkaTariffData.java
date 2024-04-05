package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class KafkaTariffData {

    @JsonProperty("tariff_id")
    private Integer tariffId;

    @JsonProperty("period_id")
    private Integer periodId;

    public KafkaTariffData(Integer tariffId, Integer periodId) {
        this.tariffId = tariffId;
        this.periodId = periodId;
    }
}
