package tgc.plus.authservice.dto.tokens_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@NoArgsConstructor
public class UpdateTokenResponse {

    private String accessToken;
    private String refreshToken;

    public UpdateTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
