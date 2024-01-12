package tgc.plus.authservice.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class DeviceTokens {

    public interface AllView{}
    public interface OnlyTokens extends AllView{}
    String accessToken;
    String refreshToken;

    public DeviceTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    @JsonView(AllView.class)
    public String getAccessToken() {
        return accessToken;
    }
    @JsonView(AllView.class)
    public String getRefreshToken() {
        return refreshToken;
    }
}
