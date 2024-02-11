package tgc.plus.callservice.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import tgc.plus.callservice.configs.KafkaConsumerConfig;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;


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

//    @Value("${consumer.setting.commit_time}")
//    Integer commitTime;

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @Autowired
    KafkaConsumerConfig kafkaConsumerConfig;

    @EventListener(value = ApplicationStartedEvent.class)
    public Disposable kafkaConsumerStarter() {
        ReceiverOptions<Long, MessageElement> receiverOptions = kafkaConsumerConfig.receiverOptions()
                .subscription(Collections.singleton(topic))
//                .withKeyDeserializer(new LongDeserializer())
//                .withValueDeserializer(new MessageDeserializer())
                .addAssignListener(receiverPartitions -> log.info("Partitions assign {}", receiverPartitions))
                .addRevokeListener(revokeListener -> log.info("Partitions revoke {}", revokeListener))
                .commitInterval(Duration.ZERO)
                .commitBatchSize(0);
//                .commit;
//                .pauseAllAfterRebalance(true);

        KafkaReceiver<Long, MessageElement> kafkaReceiver = KafkaReceiver.create(receiverOptions);
        Scheduler scheduler = Schedulers.newBoundedElastic(10, 100000, "writerThreads");

//        Flux<GroupedFlux<TopicPartition, ReceiverRecord<Long, MessageElement>>> partitions =  Flux.defer(kafkaReceiver::receive)
//                .groupBy(elems -> elems.receiverOffset().topicPartition());

        return kafkaReceiver.receive()
                .flatMapSequential(msg ->
                commandsDispatcher.execute(msg).then(msg.receiverOffset().commit())).subscribe();
//                .flatMap(partition -> )
//                .subscribe(msg ->{
//                    commandsDispatcher.execute(msg).subscribe();
//                    log.info("Elem commit - {}", new String(msg.headers().lastHeader("id").value()));
//                    msg.receiverOffset().acknowledge();
////                    count.set(count.get() + 1);
////                    log.info("Res - " + count);
//                });
//                                        .thenReturn(msg.offset())

//        Scheduler scheduler = Schedulers.newBoundedElastic(60, 60, "writerThreads");
//
//         return receiver.receive()
//                .groupBy(m -> m.receiverOffset().topicPartition())
//                .flatMap(partitionFlux ->
//                        partitionFlux.publishOn(scheduler)
//                                .map(msg -> commandsDispatcher.execute(msg)
//                                        .thenReturn(msg.offset()))).subscribe();
//


//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
//                .doOnError(e -> log.info(e.getMessage()))
//                .retry();


//        return KafkaReceiver.create(receiverOptions)
//                .receive()
//                .groupBy(el -> el.receiverOffset().topicPartition())
//                .flatMap(partition -> partition.publishOn(customScheduler)
//                                .flatMapSequential(msg -> {
//                                    log.info("Processing {}", msg.receiverOffset().offset());
//                                    return commandsDispatcher.execute(msg).map(ReceiverOffset::commit);
//                                }))
//                .doOnError(e -> log.info(e.getMessage()));


//                .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)));
//                .doOnError(e -> log.error(e.getMessage()))
//                .retry();
//                                .sample(Duration.ofMillis(commitTime))
//                                .concatMap(commit -> commit.map(el -> {
//                                    log.info("Elem commit - " + el.offset());
//                                    return el.commit();
//                                })))

//                        .doOnError(e -> log.error(e.getMessage()));

    }
}

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
//           .repeat()







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