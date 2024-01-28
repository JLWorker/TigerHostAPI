package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
import tgc.plus.callservice.services.EmailSender;

@Component
@Slf4j
public class SendMail implements Command {

    private final EmailSender emailSender;

    private final UserRepository userRepository;

    public SendMail(EmailSender emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> execution(MessageElement messageElement) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return userRepository.getUserByUserCode(messageElement.getUserCode())
                .flatMap(user -> {
                    if(user!=null){
                        return emailSender.sendMessage(messageElement.getPayload().getData(), user.getEmail(), method);
                    }
                    else
                        return Mono.error(new UserNotFound(String.format("User with code - %s not found", messageElement.getUserCode())));
                })
                .doOnError(error -> log.error(error.getMessage()));

    }

}
