package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class VmUserSetPassword {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public VmUserSetPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
