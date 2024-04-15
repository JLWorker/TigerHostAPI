package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateToken {

    @JsonProperty("refresh_token")
    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
    message = "Invalid refresh token")
    @Schema(example = "d507935-d3ab-4a90-a33f-b7......")
    private String refreshToken;

    public UpdateToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }



}
