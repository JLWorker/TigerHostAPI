package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class MessageData {

    private String user_code;
    private String email;
    private String phone;
    private String message;

    public MessageData(String user_code, String email, String phone, String message) {
        this.user_code = user_code;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }

    public MessageData(String user_code, String message) {
        this.user_code = user_code;
        this.message = message;
    }
}
