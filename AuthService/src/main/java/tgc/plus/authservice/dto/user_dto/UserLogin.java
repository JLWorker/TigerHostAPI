package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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
    private UserData userData;

    @Valid
    @JsonProperty("device_data")
    private DeviceData deviceData;

    public UserLogin(UserData userData, DeviceData deviceData) {
        this.userData = userData;
        this.deviceData = deviceData;
    }

}
