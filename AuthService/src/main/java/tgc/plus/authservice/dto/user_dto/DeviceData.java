package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceData {

    @JsonProperty()
    @NotBlank(message = "Device name must be null or empty")
    private String name;

    @JsonProperty("application_type")
    @NotBlank(message = "Application type must be null or empty")
    private String applicationType;

    public DeviceData(String name, String applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }
}
