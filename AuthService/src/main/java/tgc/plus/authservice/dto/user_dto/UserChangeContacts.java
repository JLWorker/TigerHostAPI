package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
public class UserChangeContacts {

    public interface ChangePhone{}
    public interface ChangeEmail{}

    @JsonView(ChangePhone.class)
    @Pattern(regexp = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$",
    message = "Phone invalid")
    private String phone;

    @JsonView(ChangeEmail.class)
    @Email(message = "Email invalid")
    private String email;

    private Long version;

    public UserChangeContacts(String phone, String email, Long version) {
        this.phone = phone;
        this.email = email;
        this.version = version;
    }
}
