package tgc.plus.feedbackgateaway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessageDto;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, EventKafkaMessageDto> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory){
        Jackson2JsonRedisSerializer<EventKafkaMessageDto> serializerValue = new Jackson2JsonRedisSerializer<>(EventKafkaMessageDto.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, EventKafkaMessageDto> builder = RedisSerializationContext.newSerializationContext(StringRedisSerializer.UTF_8);
        return new ReactiveRedisTemplate<>(factory, builder.value(serializerValue).build());
    }

}
