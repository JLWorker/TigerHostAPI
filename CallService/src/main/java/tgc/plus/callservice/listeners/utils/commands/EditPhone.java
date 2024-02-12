package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
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
    public Mono<Void> execution(MessageElement messageElement) {

        return userRepository.getUserByUserCode(messageElement.getUserCode()).map(Objects::nonNull)
                .defaultIfEmpty(false)
                .flatMap(result -> {
                    if(!result)
                        return Mono.error(new UserNotFound(String.format("User with code - %s not found",  messageElement.getUserCode())));
                    else {
                        return userRepository.updatePhoneUser(messageElement.getUserCode(), messageElement.getPayload().getData().get("phone"))
                                .doOnSuccess(success -> log.info("Phone number for user with code - " + messageElement.getUserCode() + " was updated"));
                    }
                }).doOnError(error -> log.error(error.getMessage()));

    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
