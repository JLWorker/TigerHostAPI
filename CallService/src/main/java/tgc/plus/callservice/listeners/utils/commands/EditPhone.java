package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageData;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.CheckTools;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

@Slf4j
@Component
public class EditPhone implements Command {

    final UserRepository userRepository;

    final CheckTools checkTools;

    public EditPhone(UserRepository userRepository, CheckTools checkTools) {
        this.userRepository = userRepository;
        this.checkTools = checkTools;
    }

    @Override
    public void execution(MessageData messageData) {

        checkTools.checkUserByCode(messageData.getUser_code())
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if(!result)
                        throw new UserNotFound("User with code - " + messageData.getUser_code() + " not found");

                    else {
                        userRepository.updatePhoneUser(messageData.getUser_code(), messageData.getPhone())
                                .doOnSuccess(success -> log.info("Phone number for user with code - " + messageData.getUser_code() + " was updated"))
                                .doOnError(error -> log.error(error.getMessage()))
                                .subscribe();
                    }
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();

    }
}
