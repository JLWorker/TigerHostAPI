package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.NodeLoadData;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.NodeStorageData;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class NodesLoadInfo {

    @JsonProperty("data")
    private List<NodeLoadData> nodesLoads;

    public NodesLoadInfo(List<NodeLoadData> nodesLoads) {
        this.nodesLoads = nodesLoads;
    }
}
