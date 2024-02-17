package tgc.plus.authservice.dto.tokens_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@NoArgsConstructor
public class UpdateTokenResponse {

    private Map<String, String> data;

    public UpdateTokenResponse(Map<String, String> tokenPair) {
        this.data = tokenPair;
    }

}
