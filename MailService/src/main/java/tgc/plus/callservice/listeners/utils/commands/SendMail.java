package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entities.User;
import tgc.plus.callservice.exceptions.UserNotFoundException;
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
                .defaultIfEmpty(new User())
                .filter(user -> user.getId()!=null)
                .switchIfEmpty(Mono.error(new UserNotFoundException(String.format("User with code - %s not found", messageElement.getUserCode()))))
                .flatMap(user -> emailSender.sendMessage(messageElement.getPayload(), user.getEmail(), method));
    }

}
