package tgc.plus.feedbackgateaway.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import tgc.plus.feedbackgateaway.dto.EventMessage;
import tgc.plus.feedbackgateaway.facade.FeedbackFacade;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    FeedbackFacade feedbackFacade;

    @GetMapping("/events")
    public Flux<ServerSentEvent<EventMessage>> getEvents(){
        return feedbackFacade.getEventsForDevice();
    }

}
