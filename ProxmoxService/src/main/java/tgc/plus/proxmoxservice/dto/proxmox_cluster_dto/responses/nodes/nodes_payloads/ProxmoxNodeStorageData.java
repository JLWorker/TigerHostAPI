package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.nodes_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProxmoxNodeStorageData {

    @JsonProperty
    private String storage;

    @JsonProperty
    private Double avail;

    public ProxmoxNodeStorageData(String storage, Double avail) {
        this.storage = storage;
        this.avail = avail;
    }
}
