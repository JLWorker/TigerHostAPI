package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class UserLogin {

    @Valid
    @JsonProperty("user_data")
    @JsonView(UserData.LoginUserData.class)
    @NotNull(message = "User data cannot be null")
    private UserData userData;

    @Valid
    @JsonProperty("device_data")
    @NotNull(message = "Device data cannot be null")
    private DeviceData deviceData;

    public UserLogin(UserData userData, DeviceData deviceData) {
        this.userData = userData;
        this.deviceData = deviceData;
    }

}
