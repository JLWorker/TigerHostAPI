package tgc.plus.callservice.listeners;

import tgc.plus.callservice.dto.MessageData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
public class MessageListener {

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory")
    public void listen(List<Message<MessageData>> messages){
        messages.forEach(el -> System.out.println(new String((byte[]) Objects.requireNonNull(el.getHeaders().get("user_code")), StandardCharsets.UTF_8)
                + " data - " + el.getPayload().getMessage() + " : " + el.getPayload().getMethod()));
    }
}
