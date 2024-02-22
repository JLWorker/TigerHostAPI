package tgc.plus.authservice.dto.two_factor_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class TwoFactorSwitchResponse {

    @JsonProperty("user_version")
    private Long userVersion;

    public TwoFactorSwitchResponse(Long userVersion) {
        this.userVersion = userVersion;
    }
}
