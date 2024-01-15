package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@JsonTypeName(value = "EditPhoneData")
public class EditPhoneData implements Payload {

    private String phone;

    public EditPhoneData(String phone) {
        this.phone = phone;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", this.getPhone());
        return map;
    }
}
