package tgc.plus.authservice.dto.user_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserInfoResponse {

    @JsonProperty
    private String phone;

    @JsonProperty
    private String email;

    @JsonProperty("two_auth_status")
    private Boolean twoAuth;

    public UserInfoResponse(String phone, String email, Boolean twoAuth) {
        this.phone = phone;
        this.email = email;
        this.twoAuth = twoAuth;
    }
}
