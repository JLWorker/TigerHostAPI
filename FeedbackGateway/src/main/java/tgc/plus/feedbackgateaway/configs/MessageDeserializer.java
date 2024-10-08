package tgc.plus.feedbackgateaway.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessage;

@Slf4j
@Component
@NoArgsConstructor
public class MessageDeserializer implements Deserializer<EventKafkaMessage> {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public EventKafkaMessage deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                throw new NullPointerException("The message from kafka contains no load");
            else
                return objectMapper.readValue(data, EventKafkaMessage.class);
        }
        catch (Exception e){
            log.warn("Message in topic {} failed to deserialize, exception - {}", topic, e.getMessage());
            return null;
        }
    }

}
