package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePeriodDto {

    @JsonProperty("discount_percent")
    @Schema(example = "10")
    @DecimalMax(value = "100", message = "Invalid discount of percent")
    @NotNull(message = "Discount percent parameter mustn't be null")
    private Integer discountPercent;

    @JsonProperty("active")
    @Schema(example = "true")
    @NotNull(message = "Active parameter mustn't be null")
    private Boolean active;


}
