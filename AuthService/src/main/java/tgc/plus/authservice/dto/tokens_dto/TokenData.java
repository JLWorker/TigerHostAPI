package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenData {

    @JsonProperty("token_id")
    String tokenId;
    @JsonProperty("token_meta")
    TokenMetaData tokenMetaData;

    public TokenData(String tokenId, TokenMetaData tokenMetaData) {
        this.tokenId = tokenId;
        this.tokenMetaData = tokenMetaData;
    }
}
