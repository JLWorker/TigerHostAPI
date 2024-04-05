package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class NewTariff {

    public interface Create {}
    public interface Change {}

    @JsonProperty("tariff_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\d+\\s+]{3,100}$", message = "Invalid tariff name")
    @JsonView({Create.class, Change.class})
    private String tariffName;

    @JsonProperty("price_month")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid month price")
    @JsonView({Create.class, Change.class})
    private Integer priceMonthKop;

    @JsonProperty("cpu")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid cpu parameter")
    @JsonView({Create.class})
    private Integer cpu;

    @JsonProperty("ram")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid ram parameter")
    @JsonView({Create.class})
    private Integer ram;

    @JsonProperty("memory")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid memory parameter")
    @JsonView({Create.class})
    private Integer memory;

    @JsonProperty("cpu_type")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid cpuType parameter")
    @JsonView({Create.class, Change.class})
    private Integer cpuType;

    @JsonProperty("ram_type")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid ramType parameter")
    @JsonView({Create.class, Change.class})
    private Integer ramType;

    @JsonProperty("memory_type")
    @NotBlank
    @Pattern(regexp = "^\\d+$", message = "Invalid memoryType parameter")
    @JsonView({Create.class, Change.class})
    private Integer memoryType;

    @JsonProperty("active")
    @NotBlank
    @JsonView({Create.class, Change.class})
    private Boolean active;

    public NewTariff(String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, Integer cpuType, Integer ramType, Integer memoryType, Boolean active) {
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
        this.active = active;
    }


    public NewTariff(String tariffName, Integer priceMonthKop, Integer cpuType, Integer ramType, Integer memoryType, Boolean active) {
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
        this.active = active;
    }
}
