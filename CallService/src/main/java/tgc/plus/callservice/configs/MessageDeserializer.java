package tgc.plus.callservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import tgc.plus.callservice.dto.MessageElement;

public class MessageDeserializer implements Deserializer<MessageElement> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public MessageElement deserialize(String topic, byte[] data) {
        if (data==null)
            throw new NullPointerException("The message from kafka contains no load");
        else
            return objectMapper.readValue(data, MessageElement.class);
    }

}
