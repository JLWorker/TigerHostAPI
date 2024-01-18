package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineExpireData")
public class VirtualMachineExpireData implements Payload{

    Integer vmId;
    String expiredDate;
    Double price;

    public VirtualMachineExpireData(Integer vmId, String expiredDate, Double price) {
        this.vmId = vmId;
        this.expiredDate = expiredDate;
        this.price = price;
    }

    @Override
    public Map<String, String> getData() {
        Map<String, String> map = new HashMap<>();
        map.put("vm_id", this.getVmId().toString());
        map.put("expired_date", this.getExpiredDate());
        map.put("price", this.getPrice().toString());
        return map;
    }
}
