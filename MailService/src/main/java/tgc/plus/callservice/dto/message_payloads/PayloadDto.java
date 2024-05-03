package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserPayloadDto.class, name = "SaveUserData"),
        @JsonSubTypes.Type(value = VirtualMachineCreatePayloadDto.class, name = "VirtualMachineCreateData"),
        @JsonSubTypes.Type(value = VirtualMachineExpirePayloadDto.class, name = "VirtualMachineExpireData"),
        @JsonSubTypes.Type(value = PasswordRestorePayloadDto.class, name = "PasswordRecoveryData"),
        @JsonSubTypes.Type(value = EditPhonePayloadDto.class, name = "EditPhoneData"),
        @JsonSubTypes.Type(value = EditEmailPayloadDto.class, name = "EditEmailData")
})
public interface PayloadDto {
}
