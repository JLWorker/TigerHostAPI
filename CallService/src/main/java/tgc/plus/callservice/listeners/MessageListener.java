package tgc.plus.callservice.listeners;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import tgc.plus.callservice.dto.MessageElement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
    public void listen(@Payload @Valid MessageElement message, @Header(name = "method") String header){
            commandsDispatcher.execute(header, message);
    }

}
