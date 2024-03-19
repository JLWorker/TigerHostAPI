package tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class VmPartitionDiskData {

    @JsonProperty("mountpoint")
    private String mountPoint;

    @JsonProperty("total-bytes")
    private Long totalPartitionSize;

    @JsonProperty("used-bytes")
    private Long usedPartitionSize;

    public VmPartitionDiskData(String mountPoint, Long totalPartitionSize, Long usedPartitionSize) {
        this.mountPoint = mountPoint;
        this.totalPartitionSize = totalPartitionSize;
        this.usedPartitionSize = usedPartitionSize;
    }
}
