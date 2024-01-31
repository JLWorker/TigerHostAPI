package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class RegistrationTokens {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty
    private Map<String, Long> versions;

    public RegistrationTokens(String accessToken, String refreshToken, Map<String, Long> versions) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.versions = versions;
    }
}
