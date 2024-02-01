package tgc.plus.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import tgc.plus.authservice.dto.user_dto.UserChangeContacts;


@NoArgsConstructor
public class ChangePhone {

    @JsonProperty
    private String phone;

    @JsonProperty
    private Integer version;

    public String getPhone() {
        return phone;
    }

    public Integer getVersion() {
        return version;
    }

    public ChangePhone(String phone, Integer version) {
        this.phone = phone;
        this.version = version;
    }
}
