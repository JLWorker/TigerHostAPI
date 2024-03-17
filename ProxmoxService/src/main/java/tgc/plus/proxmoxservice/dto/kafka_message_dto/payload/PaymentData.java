package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PaymentData {

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty
    private Integer price;

    @JsonProperty("price_month")
    private Integer priceMonth;

    public PaymentData(String paymentId, Integer price, Integer priceMonth) {
        this.paymentId = paymentId;
        this.price = price;
        this.priceMonth = priceMonth;
    }
}
