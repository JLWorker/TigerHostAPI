package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VmUserData {

    @JsonProperty
    private String user;

    @JsonProperty("login-time")
    private Double enterTime;

    public VmUserData(String user, Double enterTime) {
        this.user = user;
        this.enterTime = enterTime;
    }
}
