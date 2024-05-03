package tgc.plus.callservice.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MailMessageDto;

@Slf4j
@Component
@NoArgsConstructor
public class MessageDeserializer implements Deserializer<MailMessageDto> {


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public MailMessageDto deserialize(String topic, byte[] data) {
        try {
            if (data == null)
                throw new NullPointerException("The message from kafka contains no load");
            else
                return objectMapper.readValue(data, MailMessageDto.class);
        }
        catch (Exception e){
            log.warn("Message in topic {} failed to deserialize, exception - {}", topic, e.getMessage());
            return null;
        }
    }

}
