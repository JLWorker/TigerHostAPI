package tgc.plus.callservice.listeners.utils;

import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MailMessageDto;

public interface Command {
     Mono<Void> execution(MailMessageDto mailMessageDto);

     Mono<Void> executionForSender(String method, MailMessageDto mailMessageDto);
}
