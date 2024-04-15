package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class TokenData {

    @JsonProperty("token_id")
    @Schema(example = "ID-9832323...")
    private String tokenId;

    @JsonProperty("device_name")
    @Schema(example = "Xiaomi Redmi")
    private String deviceName;

    @JsonProperty("application_type")
    @Schema(example = "mobile")
    private String applicationType;

    @JsonProperty("device_ip")
    @Schema(example = "192.68.39.3")
    private String deviceIp;

    public TokenData(String tokenId, String deviceName, String applicationType, String deviceIp) {
        this.tokenId = tokenId;
        this.deviceName = deviceName;
        this.applicationType = applicationType;
        this.deviceIp = deviceIp;
    }
}
