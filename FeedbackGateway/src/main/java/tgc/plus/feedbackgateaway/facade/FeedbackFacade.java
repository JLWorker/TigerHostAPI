package tgc.plus.feedbackgateaway.facade;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.jmx.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.FixedBackOff;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
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

    @Value("${sse.interval.heartbeats.s}")
    Integer intervalHeartbeats;

    @Value("${sse.interval.events.ms}")
    Long intervalEvent;

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    ReactiveRedisTemplate<String, EventMessage> redisTemplate;

    public Flux<ServerSentEvent<EventMessage>> getEventsForDevice(){

        Flux<ServerSentEvent<EventMessage>> heartbeatsEvents = Flux.interval(Duration.ofSeconds(intervalHeartbeats))
                .flatMap(el -> facadeUtils.createEvent(EventTypes.HEARTBEATS, null));

        Flux<ServerSentEvent<EventMessage>> messageEvents = Flux.interval(Duration.ofMillis(intervalEvent))
                .flatMap(el ->facadeUtils.getUserContext().flatMapMany(userCode -> redisTemplate.scan(ScanOptions.scanOptions().match(String.format("%s-*", userCode)).build()))
                        .flatMapSequential(key -> redisTemplate.opsForValue().get(key)
                                .flatMap(value -> facadeUtils.createEvent(EventTypes.SIMPLE, value))));

        return heartbeatsEvents.mergeWith(messageEvents);
    }

}
