package tgc.plus.feedbackgateaway.facade.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessageDto;

import java.time.Instant;

@Component
@Slf4j
public class FacadeUtils {

    public Mono<String> getUserContext(){
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.just(securityContext.getAuthentication().getPrincipal().toString()));
    }

    public Mono<ServerSentEvent<EventKafkaMessageDto>> createEvent(EventTypes type, EventKafkaMessageDto eventKafkaMessageDto){
        if (type.equals(EventTypes.HEARTBEATS)){
            return Mono.just(ServerSentEvent.<EventKafkaMessageDto>builder().event(EventTypes.HEARTBEATS.getName()).build());
        }
        else
            return Mono.just(ServerSentEvent.<EventKafkaMessageDto>builder()
                    .id(Instant.now().toString())
                    .event(type.getName())
                    .data(eventKafkaMessageDto)
                    .build());
    }

}
