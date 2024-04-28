package tgc.plus.authservice.dto.admin_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.authservice.entities.User;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class UserResponseDto {

    @Schema(example = "0")
    private Long id;

    @Schema(example = "95dbe4e3-7763-4c....")
    private String userCode;

    @Schema(example = "435743@bk.ru")
    private String email;

    @Schema(example = "true")
    private Boolean active;

    @Schema(example = "true")
    private Boolean twoAuthStatus;

    @Schema(example = "USER")
    private String role;

    public UserResponseDto(User user){
        this.id=user.getId();
        this.userCode=user.getUserCode();
        this.role=user.getRole();
        this.email=user.getEmail();
        this.active=user.getActive();
        this.twoAuthStatus=user.getTwoAuthStatus();
    }


}
