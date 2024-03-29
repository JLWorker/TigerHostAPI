package tgc.plus.proxmoxservice.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;
import tgc.plus.proxmoxservice.configs.KafkaConsumerConfigs;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.KafkaProxmoxMessage;
import tgc.plus.proxmoxservice.listeners.utils.CommandsDispatcher;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
public class MessageListener {

    @Autowired
    KafkaConsumerConfigs kafkaConsumerConfigs;

    @Autowired
    CommandsDispatcher commandsDispatcher;

    @Value("${kafka.topic.proxmox-service}")
    String topic;

    @Value("${kafka.listener.concurrency}")
    Integer listenerConcurrency;

    @EventListener(value = ApplicationStartedEvent.class)
    public void kafkaConsumerStarter() {
        Flux.range(0, listenerConcurrency)
                .flatMap(this::startListenerPartition).subscribe();
    }

    private Flux<Void> startListenerPartition(Integer partition) {
        ReceiverOptions<String, KafkaProxmoxMessage> receiverOptions = kafkaConsumerConfigs.receiverOptions()
                .assignment(Collections.singleton(new TopicPartition(topic, partition)));

        KafkaReceiver<String, KafkaProxmoxMessage> kafkaReceiver = KafkaReceiver.create(receiverOptions);

        return kafkaReceiver.receive()
                .concatMap(msg -> {
                    String method = new String(msg.headers().lastHeader("command").value());
                    if (!method.isBlank())
                        return commandsDispatcher.execute(method, msg.value())
                                .doOnTerminate(msg.receiverOffset()::acknowledge);
                    else {
                            log.warn("Message with offset: {} has problem with key method", msg.receiverOffset().offset());
                            return Mono.empty();
                        }
                })
                .doOnError(err -> log.error(err.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(15000)))
                .retry();
    }
}

