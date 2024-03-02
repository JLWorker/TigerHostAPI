package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExistException;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
//import tgc.plus.callservice.services.CustomValidator;
import tgc.plus.callservice.services.EmailSender;
import tgc.plus.callservice.services.utils.EmailSenderCommands;

import java.util.Map;


@Slf4j
@Component
public class SaveUser implements Command{

    final UserRepository userRepository;
    final EmailSender emailSender;

    public SaveUser(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }


    @Override
    @Transactional
    public Mono<Void> execution(MessageElement messageElement) {
        Map<String, String> payload = messageElement.getPayload().getData();
        return userRepository.getUserByUserCodeForReg(messageElement.getUserCode(), payload.get("email"))
               .defaultIfEmpty(new User())
               .filter(user -> user.getId()==null)
               .switchIfEmpty(Mono.error(new UserAlreadyExistException(String.format("User with code - %s already exist", messageElement.getUserCode()))))
               .then(userRepository.save(new User(messageElement.getUserCode(), payload.get("email")))
                       .flatMap(user -> emailSender.sendMessage(payload, user.getEmail(), EmailSenderCommands.SEND_NEW_USER.getName()))
                       .then(Mono.empty()));
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
