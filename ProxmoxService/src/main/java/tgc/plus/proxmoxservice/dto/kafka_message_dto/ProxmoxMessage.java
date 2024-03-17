package tgc.plus.proxmoxservice.dto.kafka_message_dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.payload.Payload;

@NoArgsConstructor
@Setter
@Getter
public class ProxmoxMessage {

    @JsonProperty("user_code")
    private String userCode;

    @JsonProperty("payload")
    private Payload payload;

    public ProxmoxMessage(String userCode, Payload payload) {
        this.userCode = userCode;
        this.payload = payload;
    }
}
