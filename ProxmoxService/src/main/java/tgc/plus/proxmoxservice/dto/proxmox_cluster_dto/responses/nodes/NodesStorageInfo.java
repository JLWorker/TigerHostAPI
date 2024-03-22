package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.NodeStorageData;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class NodesStorageInfo {

    @JsonProperty("data")
    private List<NodeStorageData> nodesStorages;

    public NodesStorageInfo(List<NodeStorageData> nodesStorages) {
        this.nodesStorages = nodesStorages;
    }
}
