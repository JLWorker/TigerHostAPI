package tgc.plus.callservice.listeners.utils;

import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;

public interface Command {
     Mono<Void> execution(MessageElement messageElement);

     Mono<Void> executionForSender(String method, MessageElement messageElement);
}
