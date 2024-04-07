package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FinalPrice {

    @JsonProperty("tariff_id")
    @Pattern(regexp = "^\\d+$", message = "Invalid tariff id")
    @NotBlank
    private Integer tariffId;

    @JsonProperty("period_id")
    @Pattern(regexp = "^\\d+$", message = "Invalid period id")
    @NotBlank
    private Integer periodId;

    @JsonProperty("oc_id")
    @Pattern(regexp = "^\\d+$", message = "Invalid oc id")
    @NotBlank
    private Integer ocId;


}
