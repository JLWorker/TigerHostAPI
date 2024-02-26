package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class TokensDataResponse {

    @JsonProperty("current_token_metadata")
    TokenMetaData tokenMetaData;

    @JsonProperty("account_tokens")
    List<TokenData> userActiveTokens;

    public TokensDataResponse(TokenMetaData tokenMetaData, List<TokenData> userActiveTokens) {
        this.tokenMetaData = tokenMetaData;
        this.userActiveTokens = userActiveTokens;
    }
}
