package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.Period;

@Getter
@Setter
@NoArgsConstructor
public class PeriodsDto {

    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @Pattern(regexp = "^(?:[1-9]|[12][0-9]|3[01])$|^32$", message = "Invalid count of month")
    @JsonProperty("count_month")
    @NotNull
    @Schema(example = "6")
    private Integer countMonth;

    @Pattern(regexp = "^(?:[1-9]|[1-9][1-9]|100)$", message = "Invalid discount percent")
    @JsonProperty("discount_percent")
    @NotNull
    @Schema(example = "10")
    private Integer discountPercent;

    @JsonProperty("active")
    @Schema(example = "true")
    @NotNull
    private Boolean active;

    public PeriodsDto(Period period) {
        this.id = period.getId();
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
        this.active = period.getActive();
    }
}
