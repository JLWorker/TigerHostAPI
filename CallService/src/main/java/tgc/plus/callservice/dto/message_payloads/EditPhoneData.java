package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(value = "EditPhoneData")
public class EditPhoneData implements Payload {

    @JsonProperty
    private String phone;
    public EditPhoneData(String phone) {
        this.phone = phone;
    }
}
