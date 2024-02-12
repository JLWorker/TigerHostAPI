package tgc.plus.callservice.listeners;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.AcknowledgingConsumerAwareMessageListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.ConsumerAwareMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

import java.util.List;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
    public void listen(@Payload @Valid MessageElement message, @Header(name = "method") String header){

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        commandsDispatcher.execute(header, message).subscribe();
    }

//    @Override
//    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
//    public void onMessage(List<ConsumerRecord<Long, MessageElement>> data) {
//        for (ConsumerRecord<Long, MessageElement> el : data)
//            commandsDispatcher.execute(new String(el.headers().lastHeader("method").value()), el.value()).subscribe();
//    }

//    @Override
//    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
//    public void onMessage(ConsumerRecord<Long, MessageElement> data) {
//        commandsDispatcher.execute(new String(data.headers().lastHeader("method").value()), data.value()).subscribe();
//    }



//    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
//    @Override
//    public void onMessage(ConsumerRecord<Long, MessageElement> data, Acknowledgment acknowledgment, Consumer<?,?> consumer) {
//        commandsDispatcher.execute(new String(data.headers().lastHeader("method").value()), data.value(), acknowledgment).subscribe();
//
//    }
}
