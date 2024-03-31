package tgc.plus.callservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.callservice.PayloadTest;

@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineExpireData")
public class VirtualMachineExpireDataTest implements PayloadTest {

    @JsonProperty("vm_id")
    Integer vmId;

    @JsonProperty("expired_date")
    String expiredDate;

    @JsonProperty("price")
    Double price;

    public VirtualMachineExpireDataTest(Integer vmId, String expiredDate, Double price) {
        this.vmId = vmId;
        this.expiredDate = expiredDate;
        this.price = price;
    }

}
