package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@JsonTypeName(value = "TwoAuthData")
@Getter
@NoArgsConstructor
public class TwoAuthDataTest implements PayloadTest {

    @JsonProperty
    private Integer code;

    public TwoAuthDataTest(Integer code) {
        this.code = code;
    }

}
