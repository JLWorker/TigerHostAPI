package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserData {

    public interface RegistrationData {}
    public interface LoginData {}

    @JsonProperty()
    @Email(message = "Email invalid")
    @JsonView({RegistrationData.class, LoginData.class})
    private String email;

    @JsonProperty()
    @JsonView({RegistrationData.class, LoginData.class})
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{8,20}$",
            message = "Password invalid")
    private String password;

    @JsonProperty("password_confirm")
    @JsonView(RegistrationData.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{8,20}$",
            message = "Password invalid")
    private String passwordConfirm;

    public UserData(String email, String password, String passwordConfirm) {
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
