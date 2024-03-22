package tgc.plus.proxmoxservice.dto.vm_controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class VmTimestampsResponse {

    @JsonProperty("start_date")
    private Instant startDate;

    @JsonProperty("expire_date")
    private Instant expireDated;

    public VmTimestampsResponse(Instant startDate, Instant expireDated) {
        this.startDate = startDate;
        this.expireDated = expireDated;
    }
}
