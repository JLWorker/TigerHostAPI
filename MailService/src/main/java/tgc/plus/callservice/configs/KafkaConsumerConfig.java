package tgc.plus.callservice.configs;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;
import tgc.plus.callservice.dto.MessageElement;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    @Value("${kafka.bootstrap-servers}")
    private String server;

    @Value("${kafka.consumer.group-id}")
    private String group;

    @Value("${kafka.max.pool}")
    private Integer poolRecords;

    @Bean
    public Map<String, Object> consumerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);//макс интервал между pool
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, 360000);//максимальное время ожидания сообщений от брокера
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 20000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 2000);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, poolRecords);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.CooperativeStickyAssignor");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }


    @Bean
    public ReceiverOptions<String, MessageElement> receiverOptions(){
        ReceiverOptions<String, MessageElement> receiverOptions = ReceiverOptions.create(consumerProps());
        receiverOptions.addAssignListener(receiverPartitions -> log.info("Partitions assign {}", receiverPartitions))
                .addRevokeListener(revokeListener -> log.info("Partitions revoke {}", revokeListener))
                .commitInterval(Duration.ofMillis(1000))
                .commitBatchSize(50)
                .pauseAllAfterRebalance(true);
        return receiverOptions;
    }


}

