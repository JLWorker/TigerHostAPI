package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonTypeName(value = "EditEmailData")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
public class EditEmailData implements Payload {

    private String email;

    public EditEmailData(String email) {
        this.email = email;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("email", this.getEmail());
        return map;
    }
}
