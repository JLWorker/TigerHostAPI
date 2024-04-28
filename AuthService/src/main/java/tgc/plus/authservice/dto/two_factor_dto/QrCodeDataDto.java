package tgc.plus.authservice.dto.two_factor_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class QrCodeDataDto {

    @JsonProperty("qr_code")
    @Schema(example = "iVBORw0KGgoAAAANSUhEUgAAAV4AAAFeAQAAAADlUEq3AAAD......")
    private byte[] qrCode;

    @JsonProperty("type")
    @Schema(example = "image/png")
    private String type;

    public QrCodeDataDto(byte[] qrCode, String type) {
        this.qrCode = qrCode;
        this.type = type;
    }
}
