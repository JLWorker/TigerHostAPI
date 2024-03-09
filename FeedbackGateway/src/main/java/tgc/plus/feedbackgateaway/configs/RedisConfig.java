package tgc.plus.feedbackgateaway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessage;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, EventKafkaMessage> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory){
        Jackson2JsonRedisSerializer<EventKafkaMessage> serializerValue = new Jackson2JsonRedisSerializer<>(EventKafkaMessage.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, EventKafkaMessage> builder = RedisSerializationContext.newSerializationContext(StringRedisSerializer.UTF_8);
        return new ReactiveRedisTemplate<>(factory, builder.value(serializerValue).build());
    }

}
