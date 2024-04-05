package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewPeriod {

    @Pattern(regexp = "^(?:[1-9]|[12][0-9]|3[01])$|^32$", message = "Invalid count of month")
    @JsonProperty("count_month")
    private Integer countMonth;

    @Pattern(regexp = "^(?:[1-9]|[1-9][1-9]|100)$", message = "Invalid discount percent")
    @JsonProperty("discount_percent")
    private Integer discountPercent;

    @JsonProperty("active")
    @NotBlank
    private Boolean active;

}
