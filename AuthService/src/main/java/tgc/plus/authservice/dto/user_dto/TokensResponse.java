package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class TokensResponse {

    @JsonProperty("access_token")
    @Schema(example = "asdadsadEa43sdasdc86328ksn.....")
    private String accessToken;

    @JsonProperty("refresh_token")
    @Schema(example = "d507935-d3ab-4a90-a33f-b7......")
    private String refreshToken;

    @JsonProperty("token_id")
    @Schema(example = "ID-74683729...")
    private String tokenId;

    @JsonProperty("user_version")
    @Schema(example = "1")
    private Long userVersion;

    public TokensResponse(String accessToken, String refreshToken, String tokenId, Long userVersion) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenId = tokenId;
        this.userVersion = userVersion;
    }
}
