package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

//    final CustomValidator customValidator;

    public SaveUser(UserRepository userRepository, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
//        this.customValidator = customValidator;
    }

    @Override
    public void execution(MessageElement messageElement) {
        userRepository.getUserByUserCode(messageElement.getUserCode()).map(Objects::nonNull)
                .defaultIfEmpty(false)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if (result)
                        throw new UserAlreadyExist("User with code - " + messageElement.getUserCode() + " already exist");

                    else
                        userRepository.save(new User(messageElement.getUserCode(), messageElement.getPayload().getData().get("email")))
                                .doOnSuccess(userBd -> {
                                    log.info("User with code - " + userBd.getUserCode() + " was save");
                                    emailSender.sendMessage(messageElement.getPayload().getData(), userBd.getEmail(), "send_user_cr");})
                                .doOnError(error -> log.error(error.getMessage()))
                                .subscribe();
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();
    }

    @Override
    public void executionForSender(String method, MessageElement messageElement) {

    }

}
