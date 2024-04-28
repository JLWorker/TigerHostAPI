package tgc.plus.authservice.dto.two_factor_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.authservice.dto.user_dto.DeviceDataDto;

@Getter
@NoArgsConstructor
public class TwoFactorCodeDto {

    @JsonProperty
    @Size(min = 6, max = 6, message = "Invalid 2fa code")
    @Schema(example = "345982")
    @NotNull
    private String code;

    @Valid
    @JsonProperty("device_data")
    @NotNull
    private DeviceDataDto deviceDataDto;

}
