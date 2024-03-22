package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.VmPartitionDiskData;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VmDiskInfo {

    @JsonProperty("result")
    private List<VmPartitionDiskData> vmDisks;

    public VmDiskInfo(List<VmPartitionDiskData> vmDisks) {
        this.vmDisks = vmDisks;
    }
}
