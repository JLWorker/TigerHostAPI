package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MessageTest {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("payload")
    private PayloadTest payload;

    public MessageTest(String userCode, PayloadTest payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
