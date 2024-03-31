package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewTariff;

@Table("vds_tariffs")
@Getter
@NoArgsConstructor
@Component
@Setter
@ToString
public class VdsTariff {

    @Id
    @Column("id")
    private Integer id;

    @Column("tariff_name")
    private String tariffName;

    @Column("price_month")
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

    public VdsTariff(String tariffName, Integer priceMonthKop, Integer cpu, Integer ram, Integer memory, Integer cpuType, Integer ramType, Integer memoryType) {
        this.tariffName = tariffName;
        this.priceMonthKop = priceMonthKop;
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
        this.cpuType = cpuType;
        this.ramType = ramType;
        this.memoryType = memoryType;
    }

    public VdsTariff(NewTariff newTariff) {
        this.tariffName = newTariff.getTariffName();
        this.priceMonthKop = newTariff.getPriceMonthKop();
        this.cpu = newTariff.getCpu();
        this.ram = newTariff.getRam();
        this.memory = newTariff.getMemory();
        this.cpuType = newTariff.getCpuType();
        this.ramType = newTariff.getRamType();
        this.memoryType = newTariff.getMemoryType();
    }


}
