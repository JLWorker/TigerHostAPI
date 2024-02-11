package tgc.plus.callservice.configs;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.kafka.receiver.MicrometerConsumerListener;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.netty.resources.ConnectionProvider;
import tgc.plus.callservice.dto.MessageElement;

import java.time.Duration;
import java.util.*;

@Configuration
public class KafkaConsumerConfig{

    @Value("${spring.kafka.bootstrap-servers}")
    String server;

    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${consumer.settings.session_timeout_ms}")
    Integer sessionMs; //broker died or lost contact

    @Value("${consumer.setting.max_pool_interval_ms}")
    Integer maxPoolIntervalMs;  //broker caught the cycle

    @Value("${consumer.settings.heartbeat_ms}")
    Integer heartbeatMs;

    @Value("${consumer.settings.fetch_bytes}")
    Integer fetchBytes;

    @Value("${consumer.setting.request_timeout_ms}")
    Integer requestTimeoutMs;

    @Value("${consumer.settings.pool.records}")
    Integer packages;

    @Bean
    public Map<String, Object> consumerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "callservice");
        props.put(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPoolIntervalMs);
//        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionMs);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, packages);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatMs);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "50");
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "300000");
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        //        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
//        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "1");
//        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "500");
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "300000");
//        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchBytes);
        return props;
    }

//    @Bean
//    public MicrometerConsumerListener micrometerConsumerListener(){
//        return new MicrometerConsumerListener()
//    }

//    @Bean
//    public PrometheusMeterRegistry prometheusMeterRegistry(){
//        PrometheusConfig p
//        PrometheusMeterRegistry config = new PrometheusMeterRegistry()
//    }

    @Bean
    public ReceiverOptions<Long, MessageElement> receiverOptions(){

        ReceiverOptions<Long, MessageElement> receiverOptions =  ReceiverOptions.create(consumerConfigs());
//        receiverOptions.consumerListener(new MicrometerConsumerListener(prometheusMeterRegistry()));
        return receiverOptions;
    }
}

