package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TariffData {

    @JsonProperty("tariff_id")
    private Integer tariffId;

    @JsonProperty("tariff_name")
    private String tariffName;

    @JsonProperty("price_month")
    private Float priceMonthRub;

    @JsonProperty("cpu")
    private Integer cpu;

    @JsonProperty("ram")
    private Integer ram;

    @JsonProperty("memory")
    private Integer memory;

    @JsonProperty("cpu_type")
    private String cpuType;

    @JsonProperty("ram_type")
    private String ramType;

    @JsonProperty("memory_type")
    private String memoryType;

    public TariffData(Integer tariffId, String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, String cpuType, String ramType, String memoryType) {
        this.tariffId = tariffId;
        this.tariffName = tariffName;
        this.priceMonthRub = (float) (priceMonthKop % 100);
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
    }
}
