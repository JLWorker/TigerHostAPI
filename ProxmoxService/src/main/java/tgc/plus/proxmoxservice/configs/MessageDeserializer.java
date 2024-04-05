package tgc.plus.proxmoxservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.KafkaProxmoxMessage;

@Component
@Slf4j
public class MessageDeserializer implements Deserializer<KafkaProxmoxMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public KafkaProxmoxMessage deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                throw new NullPointerException("The message from kafka contains no load");
            else
                return objectMapper.readValue(data, KafkaProxmoxMessage.class);
        }
        catch (Exception e){
            log.warn("Message in topic {} failed to deserialize, exception - {}", topic, e.getMessage());
            return null;
        }
    }

}
