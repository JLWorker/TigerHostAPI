package tgc.plus.proxmoxservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "vds_payment")
@Component
public class VdsPayment {

    @Id
    @Column("id")
    private Long id;

    @Column("vds_id")
    private Long vdsId;

    @Column("payment_id")
    private String paymentId;

    @Column("price")
    private Integer price;

    @Column("price_month")
    private Integer priceMonth;

    public VdsPayment(Long vdsId, String paymentId, Integer price, Integer priceMonth) {
        this.vdsId = vdsId;
        this.paymentId = paymentId;
        this.price = price;
        this.priceMonth = priceMonth;
    }
}
