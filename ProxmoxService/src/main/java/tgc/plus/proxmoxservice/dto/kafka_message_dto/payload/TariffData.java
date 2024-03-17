package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TariffData {

    @JsonProperty("tariff_id")
    private Integer tariffId;

    @JsonProperty("period_id")
    private Integer periodId;

    public TariffData(Integer tariffId, Integer periodId) {
        this.tariffId = tariffId;
        this.periodId = periodId;
    }
}
