package tgc.plus.feedbackgateaway.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import tgc.plus.feedbackgateaway.dto.EventMessage;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, EventMessage> reactiveRedisTemplate(){
        ReactiveRedisConnectionFactory connectionFactory = new LettuceConnectionFactory();
        Jackson2JsonRedisSerializer<EventMessage> serializerValue = new Jackson2JsonRedisSerializer<>(EventMessage.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, EventMessage> builder = RedisSerializationContext.newSerializationContext(StringRedisSerializer.UTF_8);
        return new ReactiveRedisTemplate<>(connectionFactory, builder.value(serializerValue).build());
    }

}
