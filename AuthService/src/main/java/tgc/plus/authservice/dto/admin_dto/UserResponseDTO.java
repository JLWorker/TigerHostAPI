package tgc.plus.authservice.dto.admin_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.authservice.entities.User;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
public class UserResponseDTO {

    private Long id;

    private String userCode;

    private String email;

    private Boolean active;

    private String role;

    public UserResponseDTO(User user){
        this.id=user.getId();
        this.userCode=user.getUserCode();
        this.role=user.getRole();
        this.email=user.getEmail();
        this.active=user.getActive();
    }


}
