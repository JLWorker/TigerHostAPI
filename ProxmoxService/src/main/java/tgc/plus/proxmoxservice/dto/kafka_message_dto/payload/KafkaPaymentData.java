package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
public class KafkaPaymentData {

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty
    private Integer price;

    @JsonProperty("price_month")
    private Integer priceMonth;

    public KafkaPaymentData(String paymentId, Integer price, Integer priceMonth) {
        this.paymentId = paymentId;
        this.price = price;
        this.priceMonth = priceMonth;
    }
}
