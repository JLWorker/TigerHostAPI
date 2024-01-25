package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegistrationTokens {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("user_version")
    private Long userVersion;

    @JsonProperty("device_version")
    private Long deviceVersion;

    @JsonProperty("token_version")
    private Long tokenVersion;

    public RegistrationTokens(String accessToken, String refreshToken, Long userVersion, Long deviceVersion, Long tokenVersion) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userVersion = userVersion;
        this.deviceVersion = deviceVersion;
        this.tokenVersion = tokenVersion;
    }
}
