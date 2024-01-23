package tgc.plus.authservice.dto.account_controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceDataDto {

    @JsonProperty
    private String name;

    @JsonProperty("application_type")
    private String applicationType;

    public DeviceDataDto(String name, String applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }
}
