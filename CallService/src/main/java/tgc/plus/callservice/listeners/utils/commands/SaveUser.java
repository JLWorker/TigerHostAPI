package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.network.Send;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.CheckTools;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
import tgc.plus.callservice.services.EmailSender;

import java.util.Map;


@Slf4j
@Component
public class SaveUser implements Command {

    final UserRepository userRepository;

    final CheckTools checkTools;

    final EmailSender emailSender;

    public SaveUser(UserRepository userRepository, CheckTools checkTools, EmailSender emailSender) {
        this.userRepository = userRepository;
        this.checkTools = checkTools;
        this.emailSender = emailSender;
    }

    @Override
    public void execution(MessageElement messageElement) {
        checkTools.checkUserByCode(messageElement.getUser_code())
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if (result)
                        throw new UserAlreadyExist("User with code - " + messageElement.getUser_code() + " already exist");

                    else
                        userRepository.save(new User(messageElement.getUser_code(), messageElement.getPayload().getData().get("email")))
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
