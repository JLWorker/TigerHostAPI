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

    @JsonProperty("token_id")
    @Schema(example = "ID-74683729...")
    private String tokenId;

    public TokensResponse(String accessToken, String tokenId) {
        this.accessToken = accessToken;
        this.tokenId = tokenId;
    }
}
