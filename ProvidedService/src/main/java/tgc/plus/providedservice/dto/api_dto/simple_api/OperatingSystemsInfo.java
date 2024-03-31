package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class OperatingSystemsInfo {

    @JsonProperty("operating_systems")
    private List<OcData> operatingSystems;

    public OperatingSystemsInfo(List<OcData> operatingSystems) {
        this.operatingSystems = operatingSystems;
    }
}
