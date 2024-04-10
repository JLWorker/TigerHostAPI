package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.HypervisorDto;

@Getter
@Setter
@NoArgsConstructor
@Table("hypervisors")
public class Hypervisor implements ProvidedServiceEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("active")
    private Boolean active;

    public Hypervisor(String name) {
        this.name = name;
        this.active = false;
    }

    public Hypervisor(HypervisorDto hypervisorDto){
        this.name = hypervisorDto.getName();
        this.active = hypervisorDto.getActive();
    }

    @Override
    public String getUniqueElement() {
        return name;
    }
}
