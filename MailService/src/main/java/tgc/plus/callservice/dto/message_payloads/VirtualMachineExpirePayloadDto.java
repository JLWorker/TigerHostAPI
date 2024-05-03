package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonTypeName(value = "VirtualMachineExpireData")
public class VirtualMachineExpirePayloadDto implements PayloadDto {

    @JsonProperty("vm_id")
    private Integer vmId;

    @JsonProperty("expired_date")
    private String expiredDate;

    @JsonProperty
    private Double price;

    public VirtualMachineExpirePayloadDto(Integer vmId, String expiredDate, Double price) {
        this.vmId = vmId;
        this.expiredDate = expiredDate;
        this.price = price;
    }
}
