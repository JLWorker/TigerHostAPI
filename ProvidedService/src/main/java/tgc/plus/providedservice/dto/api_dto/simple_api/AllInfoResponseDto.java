package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class AllInfoResponseDto {

    @JsonProperty("tariffs")
    private List<TariffDataDto> tariffs;

    @JsonProperty("periods")
    private List<PeriodDataDto> periods;

    @JsonProperty("operating_systems")
    private List<OcDataDto> operatingSystems;

    public AllInfoResponseDto(List<TariffDataDto> tariffs, List<PeriodDataDto> periods, List<OcDataDto> operatingSystems) {
        this.tariffs = tariffs;
        this.periods = periods;
        this.operatingSystems = operatingSystems;
    }
}
