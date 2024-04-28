package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceDataDto {

    @JsonProperty()
    @NotBlank(message = "Device name mustn't be null or empty")
    @Schema(example = "Xiaomi Redmi")
    private String name;

    @JsonProperty("application_type")
    @NotBlank(message = "Application type mustn't be null or empty")
    @Schema(example = "mobile")
    private String applicationType;

    public DeviceDataDto(String name, String applicationType) {
        this.name = name;
        this.applicationType = applicationType;
    }
}
