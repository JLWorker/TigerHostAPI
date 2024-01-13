package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageData;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.CheckTools;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;


@Slf4j
@Component
public class SaveUser implements Command {

    final UserRepository userRepository;

    final CheckTools checkTools;

    public SaveUser(UserRepository userRepository, CheckTools checkTools) {
        this.userRepository = userRepository;
        this.checkTools = checkTools;
    }

    @Override
    public void execution(MessageData messageData) {
        checkTools.checkUserByCode(messageData.getUser_code())
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if (result)
                        throw new UserAlreadyExist("User with code - " + messageData.getUser_code() + " already exist");

                    else
                        userRepository.save(new User(messageData.getUser_code(), messageData.getEmail()))
                                .doOnSuccess(userBd -> log.info("User with code - " + userBd.getUserCode() + " was save"))
                                .doOnError(error -> log.error(error.getMessage()))
                                .subscribe();
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();
    }
}
