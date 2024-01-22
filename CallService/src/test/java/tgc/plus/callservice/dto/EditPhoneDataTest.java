package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "EditPhoneData")
public class EditPhoneDataTest implements PayloadTest {

    @JsonProperty
    private String phone;

    public EditPhoneDataTest(String phone) {
        this.phone = phone;
    }

}
