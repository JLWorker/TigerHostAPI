package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenMetaData {

    @JsonProperty("device_name")
    @Schema(example = "Xiaomi Redmi")
    String tokenDeviceName;

    @JsonProperty("device_type")
    @Schema(example = "mobile")
    String tokenDeviceType;

    @JsonProperty("device_ip")
    @Schema(example = "192.68.39.3")
    String tokenDeviceIpAddress;

    public TokenMetaData(String tokenDeviceName, String tokenDeviceType, String tokenDeviceIpAddress) {
        this.tokenDeviceName = tokenDeviceName;
        this.tokenDeviceType = tokenDeviceType;
        this.tokenDeviceIpAddress = tokenDeviceIpAddress;
    }
}
