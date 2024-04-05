package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxmoxNodeLoadData {

    @JsonProperty("node")
    private String node;

    @JsonProperty
    private String status;

    @JsonProperty
    private Double cpu;

    @JsonProperty("mem")
    private Long ram;

    @JsonProperty("maxmem")
    private Long maxRam;

    public ProxmoxNodeLoadData(String node, String status, Double cpu, Long ram, Long maxRam) {
        this.node = node;
        this.status = status;
        this.cpu = cpu;
        this.ram = ram;
        this.maxRam = maxRam;
    }
}
