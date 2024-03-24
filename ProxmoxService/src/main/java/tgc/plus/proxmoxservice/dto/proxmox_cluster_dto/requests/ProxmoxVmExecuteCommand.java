package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class ProxmoxVmExecuteCommand {

    @JsonProperty
    private List<String> command;

    public ProxmoxVmExecuteCommand(List<String> command) {
        this.command = command;
    }
}
