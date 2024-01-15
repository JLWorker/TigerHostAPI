package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.dto.message_payloads.Payload;
import tgc.plus.callservice.dto.message_payloads.UserData;
import tgc.plus.callservice.dto.message_payloads.VirtualMachineCreateData;
import tgc.plus.callservice.dto.message_payloads.VirtualMachineExpireData;

import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Getter
public class MessageElement {

    private String user_code;

    private Payload payload;

    public MessageElement(String user_code, Payload payload) {
        this.user_code = user_code;
        this.payload = payload;
    }

}
