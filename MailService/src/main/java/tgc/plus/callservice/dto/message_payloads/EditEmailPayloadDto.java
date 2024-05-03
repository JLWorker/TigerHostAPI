package tgc.plus.callservice.dto.message_payloads;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeName(value = "EditEmailData")
@Setter
@Getter
@NoArgsConstructor
public class EditEmailPayloadDto implements PayloadDto {

    @JsonProperty
    private String email;

    public EditEmailPayloadDto(String email) {
        this.email = email;
    }
}
