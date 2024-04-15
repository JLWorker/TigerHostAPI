package tgc.plus.authservice.dto.database_dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChangeIpByTokenIdDTO {
    private Long userTokenId;

    private Long userId;

    private String deviceIp;

    public ChangeIpByTokenIdDTO(Long userTokenId, String deviceIp, Long userId) {
        this.userTokenId = userTokenId;
        this.deviceIp = deviceIp;
        this.userId = userId;
    }
}
