package tgc.plus.apigateway.dto.jwt_claims_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TwoFactorTokenClaimsDTO implements TokenClaimsDTO {

    private String deviceToken;
    public TwoFactorTokenClaimsDTO(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
