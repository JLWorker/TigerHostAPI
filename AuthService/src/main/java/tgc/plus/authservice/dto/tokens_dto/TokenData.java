package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenData {

    @JsonProperty("token_id")
    @Schema(example = "ID-9832323...")
    private String tokenId;

    @JsonProperty("token_meta")
    private TokenMetaData tokenMetaData;

    public TokenData(String tokenId, TokenMetaData tokenMetaData) {
        this.tokenId = tokenId;
        this.tokenMetaData = tokenMetaData;
    }
}
