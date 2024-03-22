package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NodeLoadData{

    @JsonProperty("node")
    private String node;

    @JsonProperty
    private String status;

    @JsonProperty
    private Double cpu;

    @JsonProperty("dick")
    private Long systemDisk;

    @JsonProperty("maxdisk")
    private Long maxSystemDisk;

    @JsonProperty("mem")
    private Long ram;

    @JsonProperty("maxmem")
    private Long maxRam;

    public NodeLoadData(String node, String status, Double cpu, Long systemDisk, Long maxSystemDisk, Long ram, Long maxRam) {
        this.node = node;
        this.status = status;
        this.cpu = cpu;
        this.systemDisk = systemDisk;
        this.maxSystemDisk = maxSystemDisk;
        this.ram = ram;
        this.maxRam = maxRam;
    }
}
