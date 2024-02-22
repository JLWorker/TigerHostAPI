package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "EditPhoneData")
public class EditPhoneData implements Payload {

    @JsonProperty
    private String phone;
    public EditPhoneData(String phone) {
        this.phone = phone;
    }

}
