package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import tgc.plus.callservice.dto.message_payloads.Payload;

@Getter
@NoArgsConstructor
@Setter
public class MessageElement {

    private String userCode;

    private Payload payload;

    @JsonCreator
    public MessageElement(@JsonProperty("user_code") String userCode, @JsonProperty("payload") Payload payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
