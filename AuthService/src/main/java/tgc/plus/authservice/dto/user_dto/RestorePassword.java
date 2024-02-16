package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class RestorePassword {

    public interface Restore{}

    public interface Check{}

    @JsonView(Restore.class)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
            message = "Email invalid")
    private String email;

    @JsonView(Check.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{8,20}$",
            message = "Password invalid")
    private String password;

    @JsonView(Check.class)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z]).[^!@#$%\\-+^&*|\\\\/\\s]{8,20}$",
            message = "Password invalid")
    private String passwordConfirm;

    @JsonView(Check.class)
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
            message = "Invalid code")
    private String code;

    public RestorePassword(String email) {
        this.email = email;
    }

    public RestorePassword(String password, String passwordConfirm, String code) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.code = code;
    }
}
