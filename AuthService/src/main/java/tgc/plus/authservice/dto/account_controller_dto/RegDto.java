package tgc.plus.authservice.dto.account_controller_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class RegDto {

    @Valid
    @JsonProperty("user_data")
    private UserDataDto userDataDto;

    @Valid
    @JsonProperty("device_data")
    private DeviceDataDto deviceDataDto;

    public RegDto(UserDataDto userDataDto, DeviceDataDto deviceDataDto) {
        this.userDataDto = userDataDto;
        this.deviceDataDto = deviceDataDto;
    }
}
