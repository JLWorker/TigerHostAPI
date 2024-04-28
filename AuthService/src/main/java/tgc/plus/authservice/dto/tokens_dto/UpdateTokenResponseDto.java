package tgc.plus.authservice.dto.tokens_dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTokenResponseDto {

    @Schema(example = "asdadsadEa43sdasdc86328ksn.....")
    private String accessToken;

    public UpdateTokenResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
