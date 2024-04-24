package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
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
    public interface AuthChange{}


    @JsonView(Restore.class)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])",
            message = "Email invalid")
    @Schema(example = "vasya@bk.ru")
    private String email;

    @JsonView({Check.class, AuthChange.class})
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Zа-яА-Я\\d+]{8,20}$",
            message = "Password invalid")
    @Schema(example = "12345")
    private String password;

    @JsonView({Check.class, AuthChange.class})
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])[a-zA-Zа-яА-Я\\d+]{8,20}$",
            message = "Password invalid")
    @JsonProperty("password_confirm")
    @Schema(example = "12345")
    private String passwordConfirm;

    @JsonView(Check.class)
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
            message = "Invalid code")
    @Schema(example = "768911")
    private String code;

    public RestorePassword(String email) {
        this.email = email;
    }

    public RestorePassword(String password, String passwordConfirm, String code) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.code = code;
    }

    public RestorePassword(String password, String passwordConfirm) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }
}
