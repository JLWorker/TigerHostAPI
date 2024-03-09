package tgc.plus.feedbackgateaway.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tgc.plus.feedbackgateaway.configs.RedisConfig;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessage;
import tgc.plus.feedbackgateaway.facade.utils.EventTypes;
import tgc.plus.feedbackgateaway.facade.utils.FacadeUtils;

import java.time.Duration;

@Service
@Slf4j
public class FeedbackFacade {

    @Autowired
    RedisConfig redisConfig;

    @Value("${sse.interval.heartbeats.s}")
    Integer intervalHeartbeats;

    @Value("${sse.interval.events.ms}")
    Long intervalEvent;

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    ReactiveRedisTemplate<String, EventKafkaMessage> redisTemplate;

    public Flux<ServerSentEvent<EventKafkaMessage>> getEventsForDevice(){

        Flux<ServerSentEvent<EventKafkaMessage>> heartbeatsEvents = Flux.interval(Duration.ofSeconds(intervalHeartbeats))
                .flatMap(el -> facadeUtils.createEvent(EventTypes.HEARTBEATS, null));

        Flux<ServerSentEvent<EventKafkaMessage>> messageEvents = Flux.interval(Duration.ofMillis(intervalEvent))
                .flatMap(el ->facadeUtils.getUserContext().flatMapMany(userCode -> redisTemplate.scan(ScanOptions.scanOptions().match(String.format("%s-*", userCode)).build()))
                        .flatMapSequential(key -> redisTemplate.opsForValue().get(key)
                                .flatMap(value -> facadeUtils.createEvent(EventTypes.SIMPLE, value))));

        return heartbeatsEvents.mergeWith(messageEvents);
    }

}
