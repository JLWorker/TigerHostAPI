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

    @Pattern(regexp = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$",
    message = "UserCode does`t match regex")
    @NotBlank(message = "UserCode must`t be null or empty")
    private String userCode;

    @Valid
    private Payload payload;

    @JsonCreator
    public MessageElement(@JsonProperty("user_code") String userCode, @JsonProperty("payload") Payload payload) {
        this.userCode = userCode;
        this.payload = payload;
    }

}
