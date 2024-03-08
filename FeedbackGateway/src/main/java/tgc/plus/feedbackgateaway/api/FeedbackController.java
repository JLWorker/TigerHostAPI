package tgc.plus.feedbackgateaway.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.kafka.receiver.KafkaReceiver;
import tgc.plus.feedbackgateaway.dto.EventMessage;
import tgc.plus.feedbackgateaway.facade.FeedbackFacade;

import java.time.Duration;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Autowired
    FeedbackFacade feedbackFacade;

    @GetMapping(value = "/events")
    public Flux<ServerSentEvent<EventMessage>> getEvents(){

        return feedbackFacade.getEventsForDevice();

    }

}
