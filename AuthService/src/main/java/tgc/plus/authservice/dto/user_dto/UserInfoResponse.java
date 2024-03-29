package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {

    @JsonProperty
    @Schema(example = "89245678795")
    private String phone;

    @JsonProperty
    @Schema(example = "vasya@bk.ru")
    private String email;

    @JsonProperty("two_auth_status")
    @Schema(example = "true")
    private Boolean twoAuth;

    public UserInfoResponse(String phone, String email, Boolean twoAuth) {
        this.phone = phone;
        this.email = email;
        this.twoAuth = twoAuth;
    }
}
