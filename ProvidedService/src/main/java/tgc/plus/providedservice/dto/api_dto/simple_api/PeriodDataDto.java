package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.providedservice.entities.Period;

@NoArgsConstructor
@Getter
public class PeriodDataDto {

    @JsonProperty("period_id")
    @Schema(example = "2")
    private Integer periodId;

    @JsonProperty("count_month")
    @Schema(example = "6")
    private Integer countMonth;

    @JsonProperty("discount_percent")
    @Schema(example = "10")
    private Integer discountPercent;

    public PeriodDataDto(Period period) {
        this.periodId = period.getId();
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
    }
}
