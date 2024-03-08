package tgc.plus.feedbackgateaway.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.util.retry.Retry;
import tgc.plus.feedbackgateaway.configs.KafkaConsumerConfig;
import tgc.plus.feedbackgateaway.configs.RedisConfig;
import tgc.plus.feedbackgateaway.dto.EventMessage;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
public class MessageListener {


    @Autowired
    KafkaConsumerConfig kafkaConsumerConfig;

    @Value("${kafka.topic}")
    String topic;

    @Value("${kafka.listener.concurrency}")
    Integer listenerConcurrency;

    @Value("${sse.key-live-time.ms}")
    Long keyLiveTime;

    @Autowired
    RedisConfig redisConfig;

    @Autowired
    ReactiveRedisTemplate<String, EventMessage> redisTemplate;

    @EventListener(value = ApplicationStartedEvent.class)
    public void kafkaConsumerStarter() {
        Flux.range(0, listenerConcurrency)
                .flatMap(this::startListenerPartition).subscribe();
    }

    private Flux<Void> startListenerPartition(Integer partition) {

        ReceiverOptions<String, EventMessage> receiverOptions = kafkaConsumerConfig.receiverOptions()
                .assignment(Collections.singleton(new TopicPartition(topic, partition)));

        KafkaReceiver<String, EventMessage> kafkaReceiver = KafkaReceiver.create(receiverOptions);

        return kafkaReceiver.receive()
                .concatMap(msg -> {
                    String userCode = new String(msg.headers().lastHeader("user_code").value());
                    if (!userCode.isBlank()) {
                        String uniqueKey = String.format("%s-%d%d", userCode, msg.receiverOffset().offset(), msg.receiverOffset().topicPartition().partition());
                        return redisTemplate.opsForValue().set(uniqueKey, msg.value(), Duration.ofMillis(keyLiveTime))
                                .doFinally(res ->
                                    msg.receiverOffset().acknowledge()).then();
                    }
                    else {
                        log.warn("Message with offset: {} have problem with key user_code", msg.receiverOffset().offset());
                        return Mono.empty();
                    }
                })
                .doOnError(err -> log.error(err.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(15000)))
                .retry();
    }
}

