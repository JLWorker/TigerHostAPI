package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
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
    @JsonView(UserData.LoginData.class)
    private UserData userData;

    @Valid
    @JsonProperty("device_data")
    private DeviceData deviceData;

    public UserLogin(UserData userData, DeviceData deviceData) {
        this.userData = userData;
        this.deviceData = deviceData;
    }

}
