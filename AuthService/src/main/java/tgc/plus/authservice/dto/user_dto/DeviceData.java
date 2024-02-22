package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceData {

    @JsonProperty()
    @NotBlank(message = "Device name mustn't be null or empty")
    private String name;

    @JsonProperty("application_type")
    @NotBlank(message = "Application type mustn't be null or empty")
    private String applicationType;

    public DeviceData(String name, String applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }
}
