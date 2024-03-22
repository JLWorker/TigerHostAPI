package tgc.plus.callservice.dto.message_payloads;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(value = "PasswordRecoveryData")
public class PasswordRestoreData implements Payload {

    @JsonProperty
    private String url;

    public PasswordRestoreData(String url) {
        this.url = url;
    }
}
