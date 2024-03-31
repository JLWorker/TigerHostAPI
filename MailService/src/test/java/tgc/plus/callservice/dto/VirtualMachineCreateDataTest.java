package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineCreateData")
public class VirtualMachineCreateDataTest implements PayloadTest {

    @JsonProperty("vm_id")
    Integer vmId;

    @JsonProperty
    String username;

    @JsonProperty
    String password;

    @JsonProperty
    Integer port;

    @JsonProperty
    String ip;

    public VirtualMachineCreateDataTest(Integer vmId, String username, String password, Integer port, String ip) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.port = port;
        this.ip = ip;
    }

}
