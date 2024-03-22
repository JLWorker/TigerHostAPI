package tgc.plus.callservice.dto.message_payloads;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
    private String email;

    public EditEmailData(String email) {
        this.email = email;
    }
}
