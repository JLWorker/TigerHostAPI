package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxmoxVmCurrentState {

    @JsonProperty("mem")
    private Long ram;

    @JsonProperty("cpu")
    private Double cpu;

    @JsonProperty("status")
    private String status;

    public ProxmoxVmCurrentState(Long ram, Double cpu, String status) {
        this.ram = ram;
        this.cpu = cpu;
        this.status = status;
    }
}
