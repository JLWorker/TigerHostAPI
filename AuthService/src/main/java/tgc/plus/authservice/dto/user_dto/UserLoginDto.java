package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class UserLoginDto {

    @Valid
    @JsonProperty("user_data")
    @JsonView(UserDataDto.LoginUserData.class)
    @NotNull(message = "User data cannot be null")
    private UserDataDto userDataDto;

    @Valid
    @JsonProperty("device_data")
    @NotNull(message = "Device data cannot be null")
    private DeviceDataDto deviceDataDto;

    public UserLoginDto(UserDataDto userDataDto, DeviceDataDto deviceDataDto) {
        this.userDataDto = userDataDto;
        this.deviceDataDto = deviceDataDto;
    }

}
