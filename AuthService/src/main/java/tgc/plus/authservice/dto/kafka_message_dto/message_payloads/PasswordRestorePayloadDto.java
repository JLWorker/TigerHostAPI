package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "PasswordRecoveryData")
public class PasswordRestorePayloadDto implements PayloadDto {

    @JsonProperty
    private String url;

    public PasswordRestorePayloadDto(String url) {
        this.url = url;
    }

}
