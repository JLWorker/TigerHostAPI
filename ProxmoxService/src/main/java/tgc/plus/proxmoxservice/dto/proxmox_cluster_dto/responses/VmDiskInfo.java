package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class VmDiskInfo {

    @JsonProperty("result")
    private List<VmPartitionDiskData> vmDisks;

    public VmDiskInfo(List<VmPartitionDiskData> vmDisks) {
        this.vmDisks = vmDisks;
    }
}
