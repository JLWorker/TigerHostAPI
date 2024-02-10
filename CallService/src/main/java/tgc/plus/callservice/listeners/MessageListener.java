package tgc.plus.callservice.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.requests.OffsetCommitResponse;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;
import tgc.plus.callservice.configs.MessageDeserializer;
import tgc.plus.callservice.configs.KafkaConsumerConfig;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
@Component
@Slf4j
public class MessageListener {


    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${consumer.settings.retries}")
    Integer retries;

    @Value("${consumer.settings.retries_backoff}")
    Integer retriesBackoff;

    @Value("${scheduler.settings.thread_cap}")
    Integer threadCap;

    @Value("${scheduler.settings.queue_cap}")
    Integer queueCap;

    @Value("${consumer.setting.commit_time}")
    Integer commitTime;

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @Autowired
    KafkaConsumerConfig kafkaConsumerConfig;

        @EventListener(value = ApplicationStartedEvent.class)
        public Flux<?> kafkaConsumerStarter() {
        ReceiverOptions<Long, MessageElement> receiverOptions = kafkaConsumerConfig.receiverOptions()
                .subscription(Collections.singleton(topic))
                .withKeyDeserializer(new LongDeserializer())
                .withValueDeserializer(new MessageDeserializer())
                .addAssignListener(receiverPartitions -> log.info("Partitions assign {}", receiverPartitions))
                .addRevokeListener(revokeListener -> log.info("Partitions revoke {}", revokeListener ))
                .commitInterval(Duration.ZERO);
//                .commitBatchSize(20);
//                .pauseAllAfterRebalance(true);
        Scheduler customScheduler = Schedulers.newBoundedElastic(threadCap, queueCap, "send_service_scheduler", 20, true);
        KafkaReceiver<Long, MessageElement> kafkaReceiver = KafkaReceiver.create(receiverOptions);


        return Flux.defer(kafkaReceiver::receive)
                 .groupBy(el -> el.receiverOffset().topicPartition())
                .flatMap(partition -> partition.publishOn(customScheduler).
                        concatMap(msg -> commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value())
                                    .thenReturn(msg.receiverOffset())
                        )
                        .sample(Duration.ofMillis(commitTime))
                        .concatMap(ReceiverOffset::commit))
                        .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
                        .retry();


//                        .concatMap(el -> {
//                            log.info(el.topicPartition().partition() + " : " + el.offset());
//                            return el.commit();
////                        }));
//                .doOnError(e -> log.info(e.getMessage()))
//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .retry();
//                            return commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value())
//                                    .thenReturn(msg.receiverOffset());
//                                }))
//                        .sample(Duration.ofMillis(commitTime))
//                        .concatMap(el -> {
//                            log.info(el.topicPartition().partition() + " : " + el.offset());
//                            return el.commit();
//                        }));
//                 .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//           .repeat();
    }

}







//@Component
//@Slf4j
//public class MessageListener {
//
//
//    @Value("${spring.kafka.topic}")
//    String topic;
//
//    @Value("${consumer.retries}")
//    Integer retries;
//
//    @Value("${consumer.retries_backoff}")
//    Integer retriesBackoff;
//
//    @Value("${consumer.pool.records}")
//    Integer packages;
//
//    @Autowired
//    CommandsDispatcher commandsDispatcher;
//
//    @Autowired
//    KafkaConsumerConfig kafkaConsumerConfig;
//
//    @EventListener(value = ApplicationStartedEvent.class)
//    public Flux<?> kafkaConsumerStarter() {
//        ReceiverOptions<Long, MessageElement> receiverOptions = kafkaConsumerConfig.receiverOptions().subscription(Collections.singleton(topic));
//        receiverOptions.withKeyDeserializer(new LongDeserializer());
//        receiverOptions.withValueDeserializer(new MessageDeserializer());
//        receiverOptions.commitBatchSize(packages);
//        receiverOptions.commitInterval(Duration.ZERO);
//
//        KafkaReceiver<Long, MessageElement> kafkaReceiver = KafkaReceiver.create(receiverOptions);
//        return kafkaReceiver
//                .receiveBatch(packages)
////                .publishOn(Schedulers.boundedElastic())
//                .flatMapSequential(batch -> batch)
//                .flatMapSequential(msg -> commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value())
//                        .doOnSuccess(res -> msg.receiverOffset().acknowledge())
//                        .doOnError(e -> {
//                            log.error(e.getMessage());
//                            msg.receiverOffset().acknowledge();
//                        }))
//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .repeat();
//    }
//
//}



//         res.subscribe()
////                .flatMapSequential(msg -> commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value()))
////                .then()

//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .repeat();




//        .doOnSuccess(res -> msg.receiverOffset().acknowledge())
//                    .doOnError(e -> {
//                        log.error(e.getMessage());
//                        msg.receiverOffset().acknowledge();
//                    }))


//                .receive(packages)
//                .flatMapSequential(msg -> commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value())
//                        .doOnError(e -> log.error(e.getMessage()))
//                        .doOnSuccess(res -> msg.receiverOffset().acknowledge()))
//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .repeat();


//                .receiveBatch(packages)
//                .flatMapSequential(batch -> batch)
//                .flatMapSequential(msg -> commandsDispatcher.execute(new String(msg.headers().lastHeader("method").value()), msg.value())
////                        .onErrorResume(e -> {
////                                log.error(e.getMessage());
////                                msg.receiverOffset().acknowledge();
////                                return Mono.empty();
////                        })
//                        .doOnError(e -> log.error(e.getMessage()))
//                        .doOnSuccess(res -> msg.receiverOffset().acknowledge()))
//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .repeat();