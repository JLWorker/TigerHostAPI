package tgc.plus.proxmoxservice.dto.vm_controller_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "vm_874827832")
    private String vmId;

    @JsonProperty
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]{1,32}$", message = "Username invalid")
    @Schema(example = "root")
    private String username;

    @JsonProperty
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{5,20}$",
            message = "Password invalid")
    @Schema(example = "12345")
    private String password;

    @JsonProperty("password_confirm")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{5,20}$",
            message = "Password invalid")
    @Schema(example = "12345")
    private String passwordConfirm;

    public UserChangePassword(String vmId, String username, String password, String passwordConfirm) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
