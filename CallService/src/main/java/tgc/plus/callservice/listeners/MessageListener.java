package tgc.plus.callservice.listeners;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
//import tgc.plus.callservice.listeners.utils.CommandsDispatcher;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;
import tgc.plus.callservice.listeners.utils.commands.SaveUser;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory")
    public void listen(@Payload MessageElement message, @Header(name = "method") String header, Acknowledgment ack){
        commandsDispatcher.execute(header, message).subscribe();
        ack.acknowledge();
    }

//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    EmailSender emailSender;

//    @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory")
//    public void listen(List<Message<MessageElement>> messages, Acknowledgment ack) {

//        for (ConsumerRecord<String, MessageElement> msg : messages) {
//            commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value()).subscribe();
//        }
//        AtomicInteger counter = new AtomicInteger();
//         Flux<Void> flux = Flux.fromIterable(messages)
//                .concatMap(el -> saveUser.execution(el.getPayload(), ack)
//                        .then());
////                        .doOnSuccess(res -> {
//////                            ack.acknowledge();
////                            log.info(res.getUserCode());
////                        }));
//        flux.subscribe();
//        batch.subscribe();
//        batch.doFinally(res ->ack.acknowledge()).subscribe();
//                .then(Mono.fromRunnable(ack::acknowledge)).subscribe();

//        Flux<Void> elems = Flux.range(0, messages.size())
//                .concatMap(el -> commandsDispatcher.execute(new String((byte[])messages.get(el).getHeaders().get("method")), messages.get(el).getPayload()));
//        elems.subscribe();


//
//                        .doFinally(res -> ack.acknowledge());
//        elems.subscribe();
//                elems.doFinally(res -> ack.acknowledge()).subscribe();
//         Flux.fromArray(messages.toArray())
//                         .concatMap(el -> commandsDispatcher.execute(new String((Message)el.get("method")), el.getPayload()))
//                                 .subscribe();

//    @Override
//    public void onMessage(List<ConsumerRecord<String, MessageElement>> data) {
//        for (ConsumerRecord<String, MessageElement> msg : data) {
//            commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value()).subscribe();
//
//        }
//    }

//    @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory", errorHandler = "handlerError")
//    public Flux<Void> listen(List<Message<MessageElement>> messages, Acknowledgment ack){
//
//        return Flux.range(0, messages.size())
//                .flatMapSequential(pos -> commandsDispatcher.execute(messages.get(pos).getHeaders().get("method").toString(), messages.get(pos).getPayload())
//                        .doFinally(res -> ack.acknowledge()));
//    }

    }
