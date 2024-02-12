package tgc.plus.callservice.configs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class CustomDeserializer implements Deserializer {
    @Override
    public Object deserialize(String topic, byte[] data) {
        try {
            return new ObjectMapper().readTree(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
