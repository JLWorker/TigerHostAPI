package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(value = "TwoAuthCode")
@Getter
@NoArgsConstructor
public class TwoAuthCode implements Payload {

    private Integer code;

    public TwoAuthCode(Integer code) {
        this.code = code;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("code", this.code.toString());
        return map;
    }
}
