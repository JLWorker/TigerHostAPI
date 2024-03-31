package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("periods")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Period {

    @Id
    @Column("id")
    private Integer id;

    @Column("count_month")
    private Integer countMonth;

    @Column("discount_percent")
    private Integer discountPercent;

    public Period(Integer countMonth, Integer discountPercent) {
        this.countMonth = countMonth;
        this.discountPercent = discountPercent;
    }
}
