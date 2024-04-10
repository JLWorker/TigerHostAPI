package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TariffDataDto {

    @JsonProperty("tariff_id")
    @Schema(example = "1")
    private Integer tariffId;

    @JsonProperty("tariff_name")
    @Schema(example = "Tariff №1")
    private String tariffName;

    @JsonProperty("price_month_kop")
    @Schema(example = "15000")
    private Integer priceMonthKop;

    @JsonProperty("hypervisor_id")
    @Schema(example = "2")
    private Integer hypervisorId;

    @JsonProperty("cpu")
    @Schema(example = "4")
    private Integer cpu;

    @JsonProperty("ram")
    @Schema(example = "16")
    private Integer ram;

    @JsonProperty("memory")
    @Schema(example = "100")
    private Integer memory;

    @JsonProperty("cpu_type")
    @Schema(example = "Ryzen 7")
    private String cpuType;

    @JsonProperty("ram_type")
    @Schema(example = "DDR4")
    private String ramType;

    @JsonProperty("memory_type")
    @Schema(example = "SSD")
    private String memoryType;

    public TariffDataDto(Integer tariffId, String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, String cpuType, String ramType, String memoryType) {
        this.tariffId = tariffId;
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
    }

}
