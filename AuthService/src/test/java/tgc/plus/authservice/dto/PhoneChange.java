package tgc.plus.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhoneChange {

    @JsonProperty
    private String phone;

    public PhoneChange(String phone) {
        this.phone = phone;
    }
}
