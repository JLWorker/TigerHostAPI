package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ProxmoxVmUserSetPassword {

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    public ProxmoxVmUserSetPassword(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
