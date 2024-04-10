package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.providedservice.entities.Hypervisor;

@Getter
@NoArgsConstructor
public class HypervisorDataDto {

    @JsonProperty("hypervisor_id")
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("hypervisor_name")
    @Schema(example = "Proxmox")
    private String name;

    public HypervisorDataDto(Hypervisor hypervisor){
        this.id = hypervisor.getId();
        this.name = hypervisor.getName();
    }

}
