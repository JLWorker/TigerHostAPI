package tgc.plus.feedbackgateaway.dto.message_payloads;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
})
public interface Payload {

}
