package tgc.plus.feedbackgateaway.facade.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessage;

import java.time.Instant;

@Component
@Slf4j
public class FacadeUtils {

    public Mono<String> getUserContext(){
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.just(securityContext.getAuthentication().getPrincipal().toString()));
    }

    public Mono<ServerSentEvent<EventKafkaMessage>> createEvent(EventTypes type, EventKafkaMessage eventKafkaMessage){
        if (type.equals(EventTypes.HEARTBEATS)){
            return Mono.just(ServerSentEvent.<EventKafkaMessage>builder().event(EventTypes.HEARTBEATS.getName()).build());
        }
        else
            return Mono.just(ServerSentEvent.<EventKafkaMessage>builder()
                    .id(Instant.now().toString())
                    .event(type.getName())
                    .data(eventKafkaMessage)
                    .build());
    }

}
