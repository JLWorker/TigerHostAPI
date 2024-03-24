package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class UserAllVms {

    @JsonProperty("user_vms")
    private List<UserVmElem> userVms;

    public UserAllVms(List<UserVmElem> userVms) {
        this.userVms = userVms;
    }
}
