package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AuthRestorePasswordResponse {

    @JsonProperty("user_version")
    private Long userVersion;

    public AuthRestorePasswordResponse(Long userVersion) {
        this.userVersion = userVersion;
    }
}
