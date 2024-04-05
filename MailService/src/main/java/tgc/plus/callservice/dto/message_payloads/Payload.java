package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserData.class, name = "SaveUserData"),
        @JsonSubTypes.Type(value = VirtualMachineCreateData.class, name = "VirtualMachineCreateData"),
        @JsonSubTypes.Type(value = VirtualMachineExpireData.class, name = "VirtualMachineExpireData"),
        @JsonSubTypes.Type(value = PasswordRestoreData.class, name = "PasswordRecoveryData"),
        @JsonSubTypes.Type(value = EditPhoneData.class, name = "EditPhoneData"),
        @JsonSubTypes.Type(value = EditEmailData.class, name = "EditEmailData")
})
public interface Payload {
}
