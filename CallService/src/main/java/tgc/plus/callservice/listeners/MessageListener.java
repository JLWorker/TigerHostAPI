package tgc.plus.callservice.listeners;

import jakarta.validation.Valid;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
//import tgc.plus.callservice.listeners.utils.CommandsDispatcher;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;
import tgc.plus.callservice.listeners.utils.commands.SaveUser;
import tgc.plus.callservice.repositories.UserRepository;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class MessageListener {

//    @Autowired
//    CommandsDispatcher commandsDispatcher;

    @Autowired
    UserRepository userRepository;

//    @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory")
//    public void listen(@Payload MessageElement message, @Header(name = "method") String header){
//        commandsDispatcher.execute(header, message).subscribe();
//
//    }

//        @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory")
//    public void listen(@Payload MessageElement message, @Header(name = "method") String header, Acknowledgment ack){
////        commandsDispatcher.execute(header, message)
////                .doAfterTerminate(ack::acknowledge).subscribe();
//            userRepository.getUserByUserCode(message.getUserCode())
//                    .defaultIfEmpty(new User())
//                    .filter(user -> user.getId()==null)
//                    .switchIfEmpty(Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", message.getUserCode()))))
//                            .then(userRepository.save(new User(message.getUserCode(), message.getPayload().getData().get("email"))))
//                    .doAfterTerminate(ack::acknowledge).subscribe();
//
//        }

//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    EmailSender emailSender;

    @Autowired
    SaveUser saveUser;

    @KafkaListener(topics = "${kafka.topic}", containerFactory = "concurrentFactory")
    public void listen(List<Message<MessageElement>> messages, Acknowledgment ack, Consumer<?, ?> consumer) {

        Flux.fromIterable(messages).concatMap(msg ->
                        saveUser.saveUser(msg.getPayload())
                       .onErrorResume(e -> {
                           log.info(e.getMessage());
                           return Mono.empty();
                       }))
                .doOnComplete(ack::acknowledge)
//                .doOnComplete(consumer::commitAsync)
                        .subscribe();
        }


//        for (Message<MessageElement> msg : messages) {
//            commandsDispatcher.execute(new String((byte[]) msg.getHeaders().get("method")), msg.getPayload()).subscribe();
//        }
//        ack.acknowledge();
    }
        //                    .doAfterTerminate(ack::acknowledge).subscribe();
//            ack.acknowledge();

//        AtomicInteger counter = new AtomicInteger();
//        Flux.fromIterable(messages)
//                .flatMap(el ->commandsDispatcher.execute(new String((byte[]) el.getHeaders().get("method")), el.getPayload())
//                .doFinally(res -> ack.acknowledge()))
//                .subscribe();
//                        .doOnSuccess(res -> {
////                            ack.acknowledge();
//                            log.info(res.getUserCode());
//                        }));
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

