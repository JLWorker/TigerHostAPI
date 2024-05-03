package tgc.plus.callservice;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tgc.plus.callservice.dto.SaveUserDataTest;
import tgc.plus.callservice.dto.message_payloads.*;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserDataTest.class, name = "SaveUserData"),
        @JsonSubTypes.Type(value = VirtualMachineCreatePayloadDto.class, name = "VirtualMachineCreateData"),
        @JsonSubTypes.Type(value = VirtualMachineExpirePayloadDto.class, name = "VirtualMachineExpireData"),
        @JsonSubTypes.Type(value = PasswordRestorePayloadDto.class, name = "PasswordRecoveryData"),
        @JsonSubTypes.Type(value = EditPhonePayloadDto.class, name = "EditPhoneData"),
        @JsonSubTypes.Type(value = EditEmailPayloadDto.class, name = "EditEmailData"),
})
public interface PayloadTest {

}
