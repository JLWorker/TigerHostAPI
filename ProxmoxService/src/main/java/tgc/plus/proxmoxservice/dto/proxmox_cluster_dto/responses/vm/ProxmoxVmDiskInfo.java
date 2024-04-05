package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.ProxmoxVmPartitionDiskData;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxmoxVmDiskInfo {

    @JsonProperty("result")
    private List<ProxmoxVmPartitionDiskData> vmDisks;

    public ProxmoxVmDiskInfo(List<ProxmoxVmPartitionDiskData> vmDisks) {
        this.vmDisks = vmDisks;
    }
}
