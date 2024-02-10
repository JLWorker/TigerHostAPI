package tgc.plus.authservice.configs;


import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${producer.buffer-memory}")
    Long bufferMemory;

    @Value("${producer.retries}")
    Integer retries;

    @Value("${producer.retries_backoff}")
    Integer retriesBackoff;



    @Bean
    public Map<String, Object> producerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retriesBackoff);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 4);
        return props;
    }

    @Bean
    public SenderOptions<Long, KafkaMessage> senderOptions(){
        return SenderOptions.create(producerConfigs());
    }

    @Bean
    public KafkaSender<Long, KafkaMessage> kafkaSender(){
        return KafkaSender.create(senderOptions());
    }


}
