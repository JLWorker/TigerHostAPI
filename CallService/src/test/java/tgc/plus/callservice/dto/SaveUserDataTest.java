package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@NoArgsConstructor
@Getter
@JsonTypeName(value = "SaveUserData")
public class SaveUserDataTest implements PayloadTest {

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    public SaveUserDataTest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

