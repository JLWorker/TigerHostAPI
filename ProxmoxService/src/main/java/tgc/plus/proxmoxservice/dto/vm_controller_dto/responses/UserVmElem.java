package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
public class UserVmElem {

    @JsonProperty("vm_id")
    private String vmId;

    @JsonProperty("expired_date")
    private Instant expiredDate;

    @JsonProperty("start_date")
    private Instant startDate;

    @JsonProperty("price_month")
    private Integer priceMonth;

    @JsonProperty("price_period")
    private Integer pricePeriod;

    @JsonProperty("tariff_id")
    private Integer tariffId;

    @JsonProperty("period_id")
    private Integer periodId;

    @JsonProperty("os_id")
    private Integer osId;

    @JsonProperty("active")
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
