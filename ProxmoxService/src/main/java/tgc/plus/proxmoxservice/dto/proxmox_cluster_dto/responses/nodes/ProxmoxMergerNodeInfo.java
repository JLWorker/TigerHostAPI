package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.ProxmoxNodeStorageData;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProxmoxMergerNodeInfo {

    private String name;

    private String status;

    private Double cpu;

    private Long availRam;

    private List<ProxmoxNodeStorageData> disks;

    public ProxmoxMergerNodeInfo(String name, String status, Double cpu, Long availRam, List<ProxmoxNodeStorageData> disks) {
        this.name = name;
        this.status = status;
        this.cpu = cpu;
        this.availRam = availRam;
        this.disks = disks;
    }
}
