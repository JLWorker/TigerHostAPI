package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class ProxmoxVmUserData {

    @JsonProperty
    @Schema(example = "root")
    private String user;

    @JsonProperty("login-time")
    @Schema(example = "1711702846.29805")
    private Double enterTime;

    public ProxmoxVmUserData(String user, Double enterTime) {
        this.user = user;
        this.enterTime = enterTime;
    }
}
