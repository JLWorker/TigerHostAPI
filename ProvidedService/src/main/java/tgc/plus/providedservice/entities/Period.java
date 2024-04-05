package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewPeriod;

@Table("periods")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Period implements TariffEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("count_month")
    private Integer countMonth;

    @Column("discount_percent")
    private Integer discountPercent;

    @Column("active")
    private Boolean active;

    public Period(Integer countMonth, Integer discountPercent) {
        this.countMonth = countMonth;
        this.discountPercent = discountPercent;
        this.active = false;
    }

    public Period(NewPeriod period){
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
        this.active = period.getActive();
    }

    @Override
    public Boolean getActiveStatus() {
        return this.getActive();
    }

    @Override
    public String getUniqueElement() {
        return this.getCountMonth().toString();
    }
}
