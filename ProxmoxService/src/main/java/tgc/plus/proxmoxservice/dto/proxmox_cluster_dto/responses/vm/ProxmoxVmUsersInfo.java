package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.ProxmoxVmUserData;

import java.util.List;

@NoArgsConstructor
@ToString
@Getter
public class ProxmoxVmUsersInfo {

    @JsonProperty("result")
    private List<ProxmoxVmUserData> vmUsers;

    public ProxmoxVmUsersInfo(List<ProxmoxVmUserData> vmUsers) {
        this.vmUsers = vmUsers;
    }
}
