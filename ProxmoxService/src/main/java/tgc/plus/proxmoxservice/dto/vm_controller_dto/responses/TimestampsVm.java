package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TimestampsVm {

    @JsonProperty("start_date")
    @Schema(example = "2024-03-28T09:30:00Z")
    private Instant startDate;

    @JsonProperty("expire_date")
    @Schema(example = "2024-03-28T09:30:00Z")
    private Instant expireDated;

    public TimestampsVm(Instant startDate, Instant expireDated) {
        this.startDate = startDate;
        this.expireDated = expireDated;
    }
}
