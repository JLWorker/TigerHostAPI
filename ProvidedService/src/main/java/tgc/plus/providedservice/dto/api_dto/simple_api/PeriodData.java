package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import tgc.plus.providedservice.entities.Period;

@NoArgsConstructor
@Getter
public class PeriodData {

    @JsonProperty("period_id")
    private Integer periodId;

    @JsonProperty("count_month")
    private Integer countMonth;

    @JsonProperty("discount_percent")
    private Integer discountPercent;

    public PeriodData(Period period) {
        this.periodId = period.getId();
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
    }
}
