package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeName(value = "NewVmData")
public class NewVmData implements Payload{

    @JsonProperty
    private Integer template;

    @JsonProperty("os_id")
    private Integer osId;

    @JsonProperty("payment_data")
    private PaymentData paymentData;

    @JsonProperty("tariff_data")
    private TariffData tariffData;

    @JsonProperty("vds_characters")
    private VdsCharacteristicData vdsCharacteristicData;

    public NewVmData(Integer template, Integer osId, PaymentData paymentData, TariffData tariffData, VdsCharacteristicData vdsCharacteristicData) {
        this.template = template;
        this.osId = osId;
        this.paymentData = paymentData;
        this.tariffData = tariffData;
        this.vdsCharacteristicData = vdsCharacteristicData;
    }

    @Override
    public Map<String, Object> getData() {
        return Map.of(
                "template", this.template,
                "os", this.osId,
                "payment", this.paymentData,
                "tariff", this.tariffData,
                "characters", this.vdsCharacteristicData
        );
    }
}
