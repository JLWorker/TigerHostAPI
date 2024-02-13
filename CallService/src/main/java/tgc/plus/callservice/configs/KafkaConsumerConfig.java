package tgc.plus.callservice.configs;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.DefaultKafkaHeaderMapper;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.ErrorHandler;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.CommandNotFound;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@Slf4j
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    String server;

//    @Value("${spring.kafka.consumer.group-id}")
//    String group;

    @Value("${spring.kafka.listener.concurrency}")
    String concurrency;

    @Bean
    public ConsumerFactory<Long, MessageElement> consumerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "call-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, MessageElement> concurrentFactory() {
        ConcurrentKafkaListenerContainerFactory<Long, MessageElement> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(Integer.getInteger(concurrency));
        factory.setBatchListener(false);
        factory.setRecordMessageConverter(new JsonMessageConverter());
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        factory.setBatchMessageConverter(new BatchMessagingMessageConverter(new JsonMessageConverter()));
        return factory;
    }

//
//    @Autowired
//    private LocalValidatorFactoryBean validator;
//
//    @Override
//    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
//        registrar.setValidator(this.validator);
//    }

    @Bean
    public KafkaListenerErrorHandler handlerError(){
        return (m,e) -> {
            log.error("Error cause: " + e.getCause());
            return null;
        };
    }

}
