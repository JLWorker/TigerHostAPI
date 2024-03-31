package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class PeriodsInfo {

    @JsonProperty("periods")
    private List<PeriodData> periods;

    public PeriodsInfo(List<PeriodData> periods) {
        this.periods = periods;
    }
}
