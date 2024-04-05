package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.ProxmoxNodeStorageData;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProxmoxNodeStoragesInfo {

    @JsonProperty("data")
    private List<ProxmoxNodeStorageData> nodesStorages;

    public ProxmoxNodeStoragesInfo(List<ProxmoxNodeStorageData> nodesStorages) {
        this.nodesStorages = nodesStorages;
    }
}
