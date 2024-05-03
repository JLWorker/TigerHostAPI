package tgc.plus.callservice.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;
import tgc.plus.callservice.configs.KafkaConsumerConfig;
import tgc.plus.callservice.dto.MailMessageDto;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.listeners.utils.CommandsDispatcher;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @Autowired
    KafkaConsumerConfig kafkaConsumerConfig;

    @Value("${kafka.topic}")
    String topic;

    @Value("${kafka.listener.concurrency}")
    Integer listenerConcurrency;

    @EventListener(value = ApplicationStartedEvent.class)
    public void kafkaConsumerStarter() {
        Flux.range(0, listenerConcurrency)
                .flatMap(this::startListenerPartition).subscribe();
    }

    private Flux<Void> startListenerPartition(Integer partition) {
        ReceiverOptions<String, MailMessageDto> receiverOptions = kafkaConsumerConfig.receiverOptions()
                .assignment(Collections.singleton(new TopicPartition(topic, partition)));

        KafkaReceiver<String, MailMessageDto> kafkaReceiver = KafkaReceiver.create(receiverOptions);

        return kafkaReceiver.receive()
                .concatMap(msg -> {
                    String method = new String(msg.headers().lastHeader("command").value());
                    if (!method.isBlank())
                        return commandsDispatcher.execute(method, msg.value())
                                .doOnTerminate(msg.receiverOffset()::acknowledge);
                        else {
                            log.warn("Message with offset: {} have problem with key method", msg.receiverOffset().offset());
                            return Mono.empty();
                        }
                })
                .doOnError(err -> log.error(err.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(15000)))
                .retry();
    }
}

