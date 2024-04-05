package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@JsonTypeName(value = "EditEmailData")
@Getter
@NoArgsConstructor
public class EditEmailDataTest implements PayloadTest {

    @JsonProperty
    private String email;

    public EditEmailDataTest(String email) {
        this.email = email;
    }

}
