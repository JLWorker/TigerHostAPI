package tgc.plus.authservice.dto.jwt_claims_dto;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccessTokenClaims implements TokenClaims{

    private String userCode;
    private String role;

    public AccessTokenClaims(String userCode, String role) {
        this.userCode = userCode;
        this.role = role;
    }

}
