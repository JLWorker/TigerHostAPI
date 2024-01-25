package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceData {

    @JsonProperty
    private String name;

    @JsonProperty("application_type")
    private String applicationType;

    public DeviceData(String name, String applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }
}
