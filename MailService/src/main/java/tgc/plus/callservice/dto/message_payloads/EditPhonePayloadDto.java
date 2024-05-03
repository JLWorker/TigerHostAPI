package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName(value = "EditPhoneData")
public class EditPhonePayloadDto implements PayloadDto {

    @JsonProperty
    private String phone;
    public EditPhonePayloadDto(String phone) {
        this.phone = phone;
    }
}
