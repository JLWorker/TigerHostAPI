package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateToken {

    @JsonProperty("refresh_token")
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
    message = "Invalid refresh token")
    private String refreshToken;

    public UpdateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
