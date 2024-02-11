package tgc.plus.authservice.configs;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JsonSerializer;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public Map<String, Object> producerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "31.200.225.93:9199");
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "testProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return props;
    }

    @Bean
    public ProducerFactory<Long, KafkaMessage> factory(){
        DefaultKafkaProducerFactory<Long, KafkaMessage> defaultKafkaProducerFactory = new DefaultKafkaProducerFactory<>(producerConfigs());
        defaultKafkaProducerFactory.setProducerPerThread(true);
//        defaultKafkaProducerFactory.setTransactionIdPrefix("tx-"+Thread.currentThread().getId());
        return defaultKafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<Long, KafkaMessage> kafkaTemplate(){
        return new KafkaTemplate<>(factory());
    }


}
