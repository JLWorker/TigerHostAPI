package tgc.plus.feedbackgateaway.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tgc.plus.feedbackgateaway.configs.RedisConfig;
import tgc.plus.feedbackgateaway.dto.EventMessage;
import tgc.plus.feedbackgateaway.facade.utils.EventTypes;
import tgc.plus.feedbackgateaway.facade.utils.FacadeUtils;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class FeedbackFacade {

    @Autowired
    RedisConfig redisConfig;

    @Value("${sse.interval.timeout.s}")
    Integer interval;

    @Autowired
    FacadeUtils facadeUtils;

    public Flux<ServerSentEvent<EventMessage>> getEventsForDevice(){

        ReactiveRedisTemplate<String, EventMessage> redisTemplate = redisConfig.reactiveRedisTemplate();

        return Flux.interval(Duration.ofSeconds(interval))
                .flatMap(el -> facadeUtils.createEvent(EventTypes.HEARTBEATS, null))
                .mergeWith(facadeUtils.getUserContext().flatMapMany(redisTemplate::keys)
                .flatMapSequential(key -> redisTemplate.opsForValue().get(key)
                        .flatMap(value -> facadeUtils.createEvent(EventTypes.SIMPLE, value))));
    }

}
