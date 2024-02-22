package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@JsonTypeName(value = "TwoAuthData")
@Setter
@Getter
@NoArgsConstructor
public class TwoAuthData implements Payload {

    @JsonProperty
    private Integer code;

    public TwoAuthData(Integer code) {
        this.code = code;
    }
}
