package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestorePassword {

    @JsonProperty
    @Email(message = "Email invalid")
    private String email;

    public RestorePassword(String email) {
        this.email = email;
    }
}
