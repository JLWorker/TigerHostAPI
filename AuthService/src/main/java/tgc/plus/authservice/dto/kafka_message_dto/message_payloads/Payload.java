package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserData.class, name = "SaveUserData"),
        @JsonSubTypes.Type(value = PasswordRestoreData.class, name = "PasswordRecoveryData"),
        @JsonSubTypes.Type(value = EditPhoneData.class, name = "EditPhoneData"),
        @JsonSubTypes.Type(value = EditEmailData.class, name = "EditEmailData"),
})
public interface Payload {

}
