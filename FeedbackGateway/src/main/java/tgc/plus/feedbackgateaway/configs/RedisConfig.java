package tgc.plus.feedbackgateaway.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import tgc.plus.feedbackgateaway.dto.EventMessage;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, EventMessage> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory){
        Jackson2JsonRedisSerializer<EventMessage> serializerValue = new Jackson2JsonRedisSerializer<>(EventMessage.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, EventMessage> builder = RedisSerializationContext.newSerializationContext(StringRedisSerializer.UTF_8);
        return new ReactiveRedisTemplate<>(factory, builder.value(serializerValue).build());
    }

}
