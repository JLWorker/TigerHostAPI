package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class NodesLoadsInfo {

    @JsonProperty("data")
    private List<NodeLoadData> nodesLoads;

}
