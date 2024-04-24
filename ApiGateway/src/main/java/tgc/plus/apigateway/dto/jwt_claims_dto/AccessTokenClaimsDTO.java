package tgc.plus.apigateway.dto.jwt_claims_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessTokenClaimsDTO implements TokenClaimsDTO {

    private String userCode;
    private String role;

    public AccessTokenClaimsDTO(String userCode, String role) {
        this.userCode = userCode;
        this.role = role;
    }

}
