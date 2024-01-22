package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonTypeName(value = "EditEmailData")
@Setter
@Getter
@NoArgsConstructor
public class EditEmailData implements Payload {

    @JsonProperty
    @Email(message = "Incorrect email")
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
