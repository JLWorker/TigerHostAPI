package tgc.plus.authservice.dto.tokens_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenMetaData {

    @JsonProperty("device_name")
    String tokenDeviceName;

    @JsonProperty("device_type")
    String tokenDeviceType;

    @JsonProperty("device_ip")
    String tokenDeviceIpAddress;

    public TokenMetaData(String tokenDeviceName, String tokenDeviceType, String tokenDeviceIpAddress) {
        this.tokenDeviceName = tokenDeviceName;
        this.tokenDeviceType = tokenDeviceType;
        this.tokenDeviceIpAddress = tokenDeviceIpAddress;
    }
}
