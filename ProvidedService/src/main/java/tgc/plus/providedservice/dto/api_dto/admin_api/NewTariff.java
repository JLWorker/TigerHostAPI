package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class NewTariff {

    @JsonProperty("tariff_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\d+\\s+]{3,100}$", message = "Invalid tariff name")
    private String tariffName;

    @JsonProperty("price_month")
    @Pattern(regexp = "^\\d+$", message = "Invalid month price")
    private Integer priceMonthKop;

    @JsonProperty("cpu")
    @Pattern(regexp = "^\\d+$", message = "Invalid cpu parameter")
    private Integer cpu;

    @JsonProperty("ram")
    @Pattern(regexp = "^\\d+$", message = "Invalid ram parameter")
    private Integer ram;

    @JsonProperty("memory")
    @Pattern(regexp = "^\\d+$", message = "Invalid memory parameter")
    private Integer memory;

    @JsonProperty("cpu_type")
    @Pattern(regexp = "^\\d+$", message = "Invalid cpuType parameter")
    private Integer cpuType;

    @JsonProperty("ram_type")
    @Pattern(regexp = "^\\d+$", message = "Invalid ramType parameter")
    private Integer ramType;

    @JsonProperty("memory_type")
    @Pattern(regexp = "^\\d+$", message = "Invalid memoryType parameter")
    private Integer memoryType;


}
