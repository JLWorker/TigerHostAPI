package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class MessageData {

    private String user_code;
    private String email;
    private String phone;
    private HashMap<String, String> message;

    public MessageData(String user_code, String email, String phone) {
        this.user_code = user_code;
        this.email = email;
        this.phone = phone;
    }

    public MessageData(String user_code, HashMap<String, String> message) {
        this.user_code = user_code;
        this.message = message;
    }
}
