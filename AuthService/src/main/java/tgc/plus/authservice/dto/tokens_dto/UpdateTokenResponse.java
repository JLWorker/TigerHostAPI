package tgc.plus.authservice.dto.tokens_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@NoArgsConstructor
public class UpdateTokenResponse {

    @Schema(example = "asdadsadEa43sdasdc86328ksn.....")
    private String accessToken;

    public UpdateTokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
