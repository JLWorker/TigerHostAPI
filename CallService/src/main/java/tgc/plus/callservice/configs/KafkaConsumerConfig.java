package tgc.plus.callservice.configs;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.kafka.receiver.ReceiverOptions;
import tgc.plus.callservice.dto.MessageElement;

import java.time.Duration;
import java.util.*;

@Configuration
public class KafkaConsumerConfig{

    @Value("${spring.kafka.bootstrap-servers}")
    String server;

    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${consumer.settings.session_ms}")
    Integer sessionMs;

    @Value("${consumer.settings.heartbeat_ms}")
    Integer heartbeatMs;

    @Value("${consumer.settings.fetch_ms}")
    Integer fetchBytes;

    @Value("${consumer.settings.pool.records}")
    Integer packages;
    @Bean
    public Map<String, Object> consumerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
//        props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, "Consumer-"+UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "360000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "20");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        //        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
//        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500");
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "300000");
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "5000");
//        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchBytes);
        return props;
    }

    @Bean
    public ReceiverOptions<Long, MessageElement> receiverOptions(){
        return ReceiverOptions.create(consumerConfigs());
    }
}

