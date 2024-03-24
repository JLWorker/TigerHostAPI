package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProxmoxVmClone {

    @JsonProperty("newid")
    private Long newId;

    @JsonProperty("full")
    private boolean fullCopy;

    public ProxmoxVmClone(Long newId, boolean fullCopy) {
        this.newId = newId;
        this.fullCopy = fullCopy;
    }
}
