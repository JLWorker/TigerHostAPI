package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class TariffsInfo {

    @JsonProperty("tariffs")
    private List<TariffData> tariffs;

    public TariffsInfo(List<TariffData> tariffs) {
        this.tariffs = tariffs;
    }
}
