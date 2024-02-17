package tgc.plus.authservice.dto.kafka_message_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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
