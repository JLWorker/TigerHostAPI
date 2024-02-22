package tgc.plus.authservice.dto.two_factor_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class QrCodeData {

    @JsonProperty("qr_code")
    private byte[] qrCode;

    @JsonProperty("type")
    private String type;

    public QrCodeData(byte[] qrCode, String type) {
        this.qrCode = qrCode;
        this.type = type;
    }
}
