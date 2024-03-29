package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;

@Getter
@NoArgsConstructor
@ToString
public class UserVmElem {

    @JsonProperty("vm_id")
    @Schema(example = "vm_8392891")
    private String vmId;

    @JsonProperty("expired_date")
    @Schema(example = "2024-03-28T09:30:00Z")
    private Instant expiredDate;

    @JsonProperty("start_date")
    @Schema(example = "2024-03-28T09:30:00Z")
    private Instant startDate;

    @JsonProperty("price_month")
    @Schema(example = "400")
    private Integer priceMonth;

    @JsonProperty("price_period")
    @Schema(example = "1800")
    private Integer pricePeriod;

    @JsonProperty("tariff_id")
    @Schema(example = "1")
    private Integer tariffId;

    @JsonProperty("period_id")
    @Schema(example = "11")
    private Integer periodId;

    @JsonProperty("os_id")
    @Schema(example = "7")
    private Integer osId;

    @JsonProperty("active")
    @Schema(example = "true")
    private Boolean active;

    public UserVmElem(String vmId, Instant expiredDate, Instant startDate, Integer priceMonth, Integer pricePeriod, Integer tariffId, Integer periodId, Integer osId, Boolean active) {
        this.vmId = vmId;
        this.expiredDate = expiredDate;
        this.startDate = startDate;
        this.priceMonth = priceMonth;
        this.pricePeriod = pricePeriod;
        this.tariffId = tariffId;
        this.periodId = periodId;
        this.osId = osId;
        this.active = active;
    }
}
