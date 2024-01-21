package tgc.plus.callservice.dto.message_payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(value = "PasswordRecoveryData")
@NoArgsConstructor
public class PasswordRestoreData implements Payload {
    String url;

    public PasswordRestoreData(String url) {
        this.url = url;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("url", this.getUrl());
        return map;
    }
}
