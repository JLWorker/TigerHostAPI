package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserData {

    public interface RegistrationData {}
    public interface LoginData {}

    @JsonProperty
//    @JsonView({RegistrationData.class, LoginData.class})
    String email;

    @JsonProperty
//    @JsonView({RegistrationData.class, LoginData.class})
    String password;

    @JsonProperty("password_confirm")
//    @JsonView(RegistrationData.class)
    String passwordConfirm;

    public UserData(String email, String password, String passwordConfirm) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
