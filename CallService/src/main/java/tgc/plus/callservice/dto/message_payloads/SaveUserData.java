package tgc.plus.callservice.dto.message_payloads;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "SaveUserData")
@Setter
public class SaveUserData implements Payload{

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    public SaveUserData(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("email", this.getEmail());
        map.put("password", this.getPassword());
        return map;
    }
}
