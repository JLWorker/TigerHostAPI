package tgc.plus.authservice.configs;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import tgc.plus.authservice.configs.utils.CustomPartitioner;
import tgc.plus.authservice.dto.kafka_message_dto.CallMessage;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${kafka.producer.retries}")
    Integer retriesConfig;

    @Value("${kafka.producer.retries_backoff}")
    Long retriesBackoff;

    @Value("${kafka.bootstrap-servers}")
    String serverConfig;

    @Value("${kafka.sender.scheduler.result.thread}")
    Integer schedulerResultThread;

    @Value("${kafka.sender.scheduler.result.queue}")
    Integer schedulerResultQueue;

    @Bean
    public Map<String, Object> producerConfigs(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, serverConfig);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        props.put(ProducerConfig.RETRIES_CONFIG, retriesConfig);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retriesBackoff);
        props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class);
        return props;
    }

    @Bean
    public SenderOptions<String, KafkaMessage> senderOptions(){
        SenderOptions<String, KafkaMessage> senderOptions = SenderOptions.create(producerConfigs());
        senderOptions.scheduler(Schedulers.newBoundedElastic(schedulerResultThread, schedulerResultQueue, "sender-option-result"));
        return senderOptions;
    }

    @Bean
    public KafkaSender<String, KafkaMessage> kafkaSender(){
        return KafkaSender.create(senderOptions());
    }

}
