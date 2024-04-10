package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChangeTariffDto {

    @JsonProperty("tariff_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\d+\\s+]{3,100}$", message = "Invalid tariff name")
    @NotBlank(message = "Tariff name parameter mustn't be null")
    @Schema(example = "Tariff №1")
    private String tariffName;

    @JsonProperty("price_month")
    @NotNull(message = "Price month parameter mustn't be null")
    @Schema(example = "15000")
    private Integer priceMonthKop;

    @JsonProperty("cpu_type")
    @NotNull(message = "Cpu type parameter mustn't be null")
    @Schema(example = "1")
    private Integer cpuType;

    @JsonProperty("ram_type")
    @NotNull(message = "Ram type parameter mustn't be null")
    @Schema(example = "1")
    private Integer ramType;

    @JsonProperty("memory_type")
    @NotNull(message = "Memory type parameter mustn't be null")
    @Schema(example = "1")
    private Integer memoryType;

    @JsonProperty("hypervisor_id")
    @NotNull(message = "Hypervisor id parameter mustn't be null")
    @Schema(example = "1")
    private Integer hypervisorId;

    @JsonProperty("active")
    @NotNull(message = "Active parameter mustn't be null")
    @Schema(example = "true")
    private Boolean active;

}
