package tgc.plus.feedbackgateaway.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessage;
import tgc.plus.feedbackgateaway.facade.FeedbackFacade;

@RestController
@RequestMapping("/feedback")
@Slf4j
public class FeedbackController {

    @Autowired
    FeedbackFacade feedbackFacade;

    @GetMapping(value = "/events")
    public Flux<ServerSentEvent<EventKafkaMessage>> getEvents(){

        return feedbackFacade.getEventsForDevice();

    }

}
