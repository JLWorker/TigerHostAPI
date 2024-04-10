package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PeriodsDto {

    @JsonProperty("id")
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("count_month")
    @DecimalMax(value = "12", message = "Invalid count of month")
    @Schema(example = "6")
    @NotNull(message = "Count month parameter mustn't be null")
    private Integer countMonth;

    @JsonProperty("discount_percent")
    @Schema(example = "10")
    @DecimalMax(value = "100", message = "Invalid discount of percent")
    @NotNull(message = "Discount percent parameter mustn't be null")
    private Integer discountPercent;

    @JsonProperty("active")
    @Schema(example = "true")
    @NotNull(message = "Active parameter mustn't be null")
    private Boolean active;

    public PeriodsDto(Period period) {
        this.id = period.getId();
        this.countMonth = period.getCountMonth();
        this.discountPercent = period.getDiscountPercent();
        this.active = period.getActive();
    }
}
