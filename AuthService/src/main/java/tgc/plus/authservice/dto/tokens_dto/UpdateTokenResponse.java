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

    @Schema(example = "d507935-d3ab-4a90-a33f-b7......")
    private String refreshToken;

    public UpdateTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
