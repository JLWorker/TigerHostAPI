package tgc.plus.proxmoxservice.dto.vm_controller_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserChangePassword {

    @JsonProperty
    @Pattern(regexp = "^vm_[0-9]{1,32}$", message = "Vm id invalid")
    private String vmId;

    @JsonProperty
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]{1,32}$", message = "Username invalid")
    private String username;

    @JsonProperty
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{5,20}$",
            message = "Password invalid")
    private String password;

    @JsonProperty("password_confirm")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{5,20}$",
            message = "Password invalid")
    private String passwordConfirm;

    public UserChangePassword(String vmId, String username, String password, String passwordConfirm) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
