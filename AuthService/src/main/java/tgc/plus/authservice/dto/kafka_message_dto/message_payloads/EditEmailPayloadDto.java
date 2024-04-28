package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeName(value = "EditEmailData")
@Getter
@NoArgsConstructor
public class EditEmailPayloadDto implements PayloadDto {

    @JsonProperty
    private String email;

    public EditEmailPayloadDto(String email) {
        this.email = email;
    }

}
