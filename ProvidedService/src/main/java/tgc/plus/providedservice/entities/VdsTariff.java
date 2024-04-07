package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import tgc.plus.providedservice.dto.api_dto.admin_api.TariffDto;

@Table("vds_tariffs")
@Getter
@NoArgsConstructor
@Component
@Setter
@ToString
public class VdsTariff implements ProvidedServiceEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("tariff_name")
    private String tariffName;

    @Column("price_month_kop")
    private Integer priceMonthKop;

    @Column("cpu")
    private Integer cpu;

    @Column("ram")
    private Integer ram;

    @Column("memory")
    private Integer memory;

    @Column("cpu_type")
    private Integer cpuType;

    @Column("ram_type")
    private Integer ramType;

    @Column("memory_type")
    private Integer memoryType;

    @Column("active")
    private Boolean active;

    public VdsTariff(String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, Integer cpuType, Integer ramType, Integer memoryType) {
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
        this.active = false;
    }

    public VdsTariff(TariffDto tariffDto) {
        this.tariffName = tariffDto.getTariffName();
        this.priceMonthKop = tariffDto.getPriceMonthKop();
        this.cpu = tariffDto.getCpu();
        this.ram = tariffDto.getRam();
        this.memory = tariffDto.getMemory();
        this.cpuType = tariffDto.getCpuType();
        this.ramType = tariffDto.getRamType();
        this.memoryType = tariffDto.getMemoryType();
        this.active = tariffDto.getActive();
    }

    @Override
    public String getUniqueElement() {
        return getTariffName();
    }

}
