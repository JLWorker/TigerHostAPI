package tgc.plus.callservice.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import tgc.plus.callservice.dto.MessageElement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
public class MessageListener {

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory")
    public void listen(List<Message<MessageElement>> messages){
        messages.forEach(el -> commandsDispatcher.execute(
                new String((byte[]) Objects.requireNonNull(el.getHeaders().get("method")), StandardCharsets.UTF_8), el.getPayload()));
    }
}
