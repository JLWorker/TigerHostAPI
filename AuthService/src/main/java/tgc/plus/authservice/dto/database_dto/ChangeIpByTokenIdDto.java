package tgc.plus.authservice.dto.database_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangeIpByTokenIdDto {
    private Long userTokenId;

    private Long userId;

    private String deviceIp;

    public ChangeIpByTokenIdDto(Long userTokenId, String deviceIp, Long userId) {
        this.userTokenId = userTokenId;
        this.deviceIp = deviceIp;
        this.userId = userId;
    }
}
