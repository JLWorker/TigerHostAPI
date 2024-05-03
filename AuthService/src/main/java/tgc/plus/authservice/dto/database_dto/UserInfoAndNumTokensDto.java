package tgc.plus.authservice.dto.database_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserInfoAndNumTokensDto {

    private Long id;

    private String userCode;

    private String email;

    private String role;

    private Boolean twoFactorStatus;

    private Integer tokensCount;

    public UserInfoAndNumTokensDto(Long id, String userCode, String email, String role, Boolean twoFactorStatus, Integer tokensCount) {
        this.id = id;
        this.userCode = userCode;
        this.email = email;
        this.role = role;
        this.twoFactorStatus = twoFactorStatus;
        this.tokensCount = tokensCount;
    }
}
