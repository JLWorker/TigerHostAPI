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

    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("tariff_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\d+\\s+]{3,100}$", message = "Invalid tariff name")
    @Schema(example = "Tariff №1")
    @NotBlank(message = "Tariff name parameter mustn't be null or empty")
    private String tariffName;

    @JsonProperty("price_month")
    @Schema(example = "15000")
    @NotNull(message = "Price month parameter mustn't be null")
    private Integer priceMonthKop;

    @JsonProperty("cpu")
    @Schema(example = "4")
    @NotNull(message = "Cpu parameter mustn't be null")
    private Integer cpu;

    @JsonProperty("ram")
    @Schema(example = "16")
    @NotNull(message = "Ram parameter mustn't be null")
    private Integer ram;

    @JsonProperty("memory")
    @Schema(example = "100")
    @NotNull(message = "Memory parameter mustn't be null")
    private Integer memory;

    @JsonProperty("cpu_type")
    @Schema(example = "1")
    @NotNull(message = "Cpu type parameter mustn't be null")
    private Integer cpuType;

    @JsonProperty("ram_type")
    @Schema(example = "1")
    @NotNull(message = "Ram type parameter mustn't be null")
    private Integer ramType;

    @JsonProperty("memory_type")
    @Schema(example = "1")
    @NotNull(message = "Memory type parameter mustn't be null")
    private Integer memoryType;

    @JsonProperty("hypervisor_id")
    @Schema(example = "1")
    @NotNull(message = "Hypervisor id parameter mustn't be null")
    private Integer hypervisorId;

    @JsonProperty("active")
    @Schema(example = "true")
    @NotNull(message = "Active parameter mustn't be null")
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
