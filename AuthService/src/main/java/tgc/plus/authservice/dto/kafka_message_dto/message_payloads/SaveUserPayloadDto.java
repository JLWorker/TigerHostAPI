package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "SaveUserData")
@Setter
public class SaveUserPayloadDto implements PayloadDto {

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    public SaveUserPayloadDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
