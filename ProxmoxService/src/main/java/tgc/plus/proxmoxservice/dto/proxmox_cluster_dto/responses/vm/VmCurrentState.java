package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmCurrentState{

    @JsonProperty("mem")
    private Long ram;

    @JsonProperty("cpu")
    private Double cpu;

    @JsonProperty("status")
    private String status;

    public VmCurrentState(Long ram, Double cpu, String status) {
        this.ram = ram;
        this.cpu = cpu;
        this.status = status;
    }
}
