package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

import java.util.Objects;

@Slf4j
@Component
public class EditPhone implements Command {

    final UserRepository userRepository;
    public EditPhone(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execution(MessageElement messageElement) {

        userRepository.getUserByUserCode(messageElement.getUserCode()).map(Objects::nonNull)
                .defaultIfEmpty(false)
                .publishOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> {
                    if(!result)
                        throw new UserNotFound("User with code - " + messageElement.getUserCode() + " not found");

                    else {
                        userRepository.updatePhoneUser(messageElement.getUserCode(), messageElement.getPayload().getData().get("phone"))
                                .doOnSuccess(success -> log.info("Phone number for user with code - " + messageElement.getUserCode() + " was updated"))
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
