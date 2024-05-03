package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UpdateTokenDto {

    @JsonProperty("token_id")
    @Schema(example = "ID-9832323...")
    @Pattern(regexp = "^ID-\\d+$", message = "Invalid token id")
    private String tokenId;

    public UpdateTokenDto(String tokenId) {
        this.tokenId = tokenId;
    }
}
