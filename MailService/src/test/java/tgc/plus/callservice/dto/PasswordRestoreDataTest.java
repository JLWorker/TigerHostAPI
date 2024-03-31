package tgc.plus.callservice.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "PasswordRecoveryData")
public class PasswordRestoreDataTest implements PayloadTest {

    @JsonProperty
    String url;

    public PasswordRestoreDataTest(String url) {
        this.url = url;
    }

}
