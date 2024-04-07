package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.VdsTariff;

@NoArgsConstructor
@Setter
@Getter
public class TariffDto {

    public interface Create {}
    public interface Change {}


    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("tariff_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\d+\\s+]{3,100}$", message = "Invalid tariff name")
    @JsonView({Create.class, Change.class})
    @Schema(example = "Tariff №1")
    private String tariffName;

    @JsonProperty("price_month")
    @JsonView({Create.class, Change.class})
    @Schema(example = "15000")
    private Integer priceMonthKop;

    @JsonProperty("cpu")
    @JsonView({Create.class})
    @Schema(example = "4")
    private Integer cpu;

    @JsonProperty("ram")
    @JsonView({Create.class})
    @Schema(example = "16")
    private Integer ram;

    @JsonProperty("memory")
    @JsonView({Create.class})
    @Schema(example = "100")
    private Integer memory;

    @JsonProperty("cpu_type")
    @JsonView({Create.class, Change.class})
    @Schema(example = "1")
    private Integer cpuType;

    @JsonProperty("ram_type")
    @JsonView({Create.class, Change.class})
    @Schema(example = "1")
    private Integer ramType;

    @JsonProperty("memory_type")
    @JsonView({Create.class, Change.class})
    @Schema(example = "1")
    private Integer memoryType;

    @JsonProperty("active")
    @JsonView({Create.class, Change.class})
    @Schema(example = "true")
    private Boolean active;

    public TariffDto(String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, Integer cpuType, Integer ramType, Integer memoryType, Boolean active) {
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

    public TariffDto(VdsTariff vdsTariff) {
        this.id = vdsTariff.getId();
        this.tariffName = vdsTariff.getTariffName();
        this.priceMonthKop = vdsTariff.getPriceMonthKop();
        this.cpu = vdsTariff.getCpu();
        this.ram = vdsTariff.getRam();
        this.memory = vdsTariff.getMemory();
        this.cpuType = vdsTariff.getCpuType();
        this.ramType = vdsTariff.getRamType();
        this.memoryType = vdsTariff.getMemoryType();
        this.active = vdsTariff.getActive();
    }

    public TariffDto(String tariffName, Integer priceMonthKop, Integer cpuType, Integer ramType, Integer memoryType, Boolean active) {
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
        this.active = active;
    }
}
