package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@JsonTypeName(value = "UserData")
public class UserData implements Payload{

    String email;
    String password;

    public UserData(String email, String password) {
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
