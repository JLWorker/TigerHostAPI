package tgc.plus.callservice.dto.message_payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.listeners.utils.Command;

import java.util.HashMap;
import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(value = "PasswordRecoveryData")
@NoArgsConstructor
public class PasswordRecoveryData implements Payload {

    String url;

    public PasswordRecoveryData(String url) {
        this.url = url;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("url", this.getUrl());
        return map;
    }
}
