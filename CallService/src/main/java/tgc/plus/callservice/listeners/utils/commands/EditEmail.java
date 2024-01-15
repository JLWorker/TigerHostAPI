package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.CheckTools;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

@Slf4j
@Component
public class EditEmail implements Command {

    final UserRepository userRepository;

    final CheckTools checkTools;

    public EditEmail(UserRepository userRepository, CheckTools checkTools) {
        this.userRepository = userRepository;
        this.checkTools = checkTools;
    }

    @Override
    public void execution(MessageElement messageElement) {
        checkTools.checkUserByCode(messageElement.getUser_code())
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if(!result)
                        throw new UserNotFound("User with code - " + messageElement.getUser_code() + " not found");

                    else {
                        userRepository.updateEmailUser(messageElement.getUser_code(), messageElement.getPayload().getData().get("email"))
                                .doOnSuccess(success -> log.info("Email for user with code - " + messageElement.getUser_code() + " was updated"))
                                .doOnError(error -> log.error(error.getMessage()))
                                .subscribe();
                    }
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();
    }

    @Override
    public void executionForSender(String method, MessageElement messageElement) {

    }

}
