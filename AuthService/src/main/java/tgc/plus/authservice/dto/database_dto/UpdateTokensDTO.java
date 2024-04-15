package tgc.plus.authservice.dto.database_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
public class UpdateTokensDTO {

    private String userCode;

    private String role;

    private Instant expiredDate;

    public UpdateTokensDTO(String userCode, String role, Instant expiredDate) {
        this.userCode = userCode;
        this.role = role;
        this.expiredDate = expiredDate;
    }
}
