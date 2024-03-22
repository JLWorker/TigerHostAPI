package tgc.plus.proxmoxservice.dto.vm_controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserChangePassword {

    @JsonProperty
    private String vmId;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty("password_confirm")
    private String passwordConfirm;

    public UserChangePassword(String vmId, String username, String password, String passwordConfirm) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
