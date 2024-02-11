package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
//import tgc.plus.callservice.services.CustomValidator;
import tgc.plus.callservice.services.EmailSender;

import java.util.Objects;


@Slf4j
@Component
public class SaveUser implements Command {

    final UserRepository userRepository;
    final EmailSender emailSender;

    public SaveUser(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
    }

    @Override
    public Mono<Void> execution(MessageElement messageElement) {
        return userRepository.getUserByUserCode(messageElement.getUserCode()).map(Objects::nonNull)
                .defaultIfEmpty(false)
                .flatMap(result -> {
                    if (result) {
                        return Mono.empty();
                    } else
                       return userRepository.save(new User(messageElement.getUserCode(), messageElement.getPayload().getData().get("email")))
                                .flatMap(userBd -> {
                                    log.info(String.format("User with code - %s was save", userBd.getUserCode()));
//                                    return Mono.empty();
//                                    return emailSender.sendMessage(messageElement.getPayload().getData(), userBd.getEmail(), "send_user_cr");
                                    return Mono.empty();
                                });
                    });
//        .doOnError(error -> log.error(error.getMessage()))
//        Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", messageElement.getUserCode())));
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
