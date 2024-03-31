package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AllInfoResponse {

    @JsonProperty("tariffs")
    private List<TariffData> tariffs;

    @JsonProperty("periods")
    private List<PeriodData> periods;

    @JsonProperty("operating_systems")
    private List<OcData> operatingSystems;

    public AllInfoResponse(List<TariffData> tariffs, List<PeriodData> periods, List<OcData> operatingSystems) {
        this.tariffs = tariffs;
        this.periods = periods;
        this.operatingSystems = operatingSystems;
    }
}
