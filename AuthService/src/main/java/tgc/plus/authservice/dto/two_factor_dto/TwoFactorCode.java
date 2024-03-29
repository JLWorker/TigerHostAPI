package tgc.plus.authservice.dto.two_factor_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.authservice.dto.user_dto.DeviceData;

@Getter
@NoArgsConstructor
public class TwoFactorCode {

    @JsonProperty
    @Size(min = 6, max = 6, message = "Invalid 2fa code")
    @Schema(example = "345982")
    private String code;

    @Valid
    @JsonProperty("device_data")
    private DeviceData deviceData;

}
