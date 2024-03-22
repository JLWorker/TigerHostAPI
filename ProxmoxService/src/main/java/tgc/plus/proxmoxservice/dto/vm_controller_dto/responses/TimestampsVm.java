package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class TimestampsVm {

    @JsonProperty("start_date")
    private Instant startDate;

    @JsonProperty("expire_date")
    private Instant expireDated;

    public TimestampsVm(Instant startDate, Instant expireDated) {
        this.startDate = startDate;
        this.expireDated = expireDated;
    }
}
