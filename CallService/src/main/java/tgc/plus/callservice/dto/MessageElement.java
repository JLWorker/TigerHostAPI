package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import tgc.plus.callservice.dto.message_payloads.Payload;

@Getter
@NoArgsConstructor
@Setter
public class MessageElement {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("payload")
    private Payload payload;

    public MessageElement(String userCode, Payload payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
