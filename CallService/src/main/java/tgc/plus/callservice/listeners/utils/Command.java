package tgc.plus.callservice.listeners.utils;

import org.springframework.kafka.support.Acknowledgment;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.dto.message_payloads.Payload;

public interface Command {
     Mono<Void> execution(MessageElement messageElement);

     Mono<Void> executionForSender(String method, MessageElement messageElement);
}
