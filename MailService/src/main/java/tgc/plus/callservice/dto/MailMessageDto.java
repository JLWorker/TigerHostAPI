package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.callservice.dto.message_payloads.PayloadDto;

@Getter
@NoArgsConstructor
@Setter
public class MailMessageDto {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("payload")
    private PayloadDto payload;

    public MailMessageDto(String userCode, PayloadDto payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
