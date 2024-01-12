package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class MessageData {
    private String method;
    private String message;

    public MessageData(String method, String message) {
        this.method = method;
        this.message = message;
    }

}
