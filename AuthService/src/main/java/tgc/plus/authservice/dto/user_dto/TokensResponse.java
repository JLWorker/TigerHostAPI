package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class TokensResponse {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("token_id")
    private String tokenId;

    @JsonProperty("user_version")
    private Long userVersion;

    public TokensResponse(String accessToken, String refreshToken, String tokenId, Long userVersion) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenId = tokenId;
        this.userVersion = userVersion;
    }
}
