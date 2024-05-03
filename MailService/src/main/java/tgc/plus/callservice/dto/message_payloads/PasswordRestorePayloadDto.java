package tgc.plus.callservice.dto.message_payloads;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(value = "PasswordRecoveryData")
public class PasswordRestorePayloadDto implements PayloadDto {

    @JsonProperty
    private String url;

    public PasswordRestorePayloadDto(String url) {
        this.url = url;
    }
}
