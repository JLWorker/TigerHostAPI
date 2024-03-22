package tgc.plus.proxmoxservice.dto.vm_controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class UserAllVmsResponse {

    @JsonProperty("user_vms")
    private List<UserVmElemResponse> userVms;

    public UserAllVmsResponse(List<UserVmElemResponse> userVms) {
        this.userVms = userVms;
    }
}
