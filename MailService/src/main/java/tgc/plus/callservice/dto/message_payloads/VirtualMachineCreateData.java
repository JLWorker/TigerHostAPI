package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineCreateData")
public class VirtualMachineCreateData implements Payload{

    @JsonProperty("vm_id")
    private Integer vmId;

    @JsonProperty
    private String username;

    @JsonProperty
    private String password;

    @JsonProperty
    private Integer port;

    @JsonProperty
    private String ip;

    public VirtualMachineCreateData(Integer vmId, String username, String password, Integer port, String ip) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.port = port;
        this.ip = ip;
    }
}
