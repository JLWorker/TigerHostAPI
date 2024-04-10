package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.PeriodsDto;

@Table("periods")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Period implements ProvidedServiceEntity {

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

    public Period(PeriodsDto period){
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
        this.active = period.getActive();
    }
    @Override
    public String getUniqueElement() {
        return this.getCountMonth().toString();
    }
}
