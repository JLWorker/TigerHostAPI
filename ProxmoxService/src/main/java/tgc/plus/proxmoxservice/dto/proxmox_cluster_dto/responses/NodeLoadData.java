package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NodeLoadData {

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
