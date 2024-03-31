package tgc.plus.callservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.kafka.listener.CommonContainerStoppingErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.UserAlreadyExistException;

@Slf4j
@Component
@NoArgsConstructor
public class MessageDeserializer implements Deserializer<MessageElement> {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MessageElement deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                throw new NullPointerException("The message from kafka contains no load");
            else
                return objectMapper.readValue(data, MessageElement.class);
        }
        catch (Exception e){
            log.warn("Message in topic {} failed to deserialize, exception - {}", topic, e.getMessage());
            return null;
        }
    }

}
