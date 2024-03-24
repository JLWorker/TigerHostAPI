package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads.ProxmoxNodeLoadData;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProxmoxNodesLoadInfo {

    @JsonProperty("data")
    private List<ProxmoxNodeLoadData> nodesLoads;

    public ProxmoxNodesLoadInfo(List<ProxmoxNodeLoadData> nodesLoads) {
        this.nodesLoads = nodesLoads;
    }
}
