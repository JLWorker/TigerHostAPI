package tgc.plus.authservice.dto.kafka_message_dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SaveUserData.class, name = "SaveUserData"),
        @JsonSubTypes.Type(value = PasswordRecoveryData.class, name = "PasswordRecoveryData")
})
public interface Payload {

}
