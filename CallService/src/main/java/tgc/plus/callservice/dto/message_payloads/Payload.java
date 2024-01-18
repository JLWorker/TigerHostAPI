package tgc.plus.callservice.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserData.class, name = "UserData"),
        @JsonSubTypes.Type(value = VirtualMachineCreateData.class, name = "VirtualMachineCreateData"),
        @JsonSubTypes.Type(value = VirtualMachineExpireData.class, name = "VirtualMachineExpireData"),
        @JsonSubTypes.Type(value = PasswordRestoreData.class, name = "PasswordRecoveryData"),
        @JsonSubTypes.Type(value = EditPhoneData.class, name = "EditPhoneData"),
})
public interface Payload {
    Map<String, String> getData();
}
