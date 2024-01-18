package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineCreateData")
public class VirtualMachineCreateData implements Payload{

    Integer vmId;
    String username;
    String password;
    Integer port;
    String ip;

    public VirtualMachineCreateData(Integer vmId, String username, String password, Integer port, String ip) {
        this.vmId = vmId;
        this.username = username;
        this.password = password;
        this.port = port;
        this.ip = ip;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("vm_id", this.getPort().toString());
        map.put("ip", this.getIp());
        map.put("port", this.getPort().toString());
        map.put("username", this.getUsername());
        map.put("password", this.getPassword());
        return map;
    }
}
