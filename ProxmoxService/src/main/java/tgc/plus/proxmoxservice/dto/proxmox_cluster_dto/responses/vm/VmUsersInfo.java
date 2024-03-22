package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.VmUserData;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class VmUsersInfo {

    @JsonProperty("result")
    private List<VmUserData> vmUsers;

    public VmUsersInfo(List<VmUserData> vmUsers) {
        this.vmUsers = vmUsers;
    }
}
