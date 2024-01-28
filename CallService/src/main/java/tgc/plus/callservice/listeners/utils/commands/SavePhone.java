package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

import java.util.Objects;

@Slf4j
@Component
public class SavePhone implements Command {

    final
    UserRepository userRepository;

    public SavePhone(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Mono<Void> execution(MessageElement messageElement) {
        return userRepository.getUserByUserCode(messageElement.getUserCode()).map(Objects::nonNull)
                .defaultIfEmpty(false)
                .flatMap(result -> {
                    if (result)
                        return Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", messageElement.getUserCode())));
                    else
                        return userRepository.updatePhoneUser(messageElement.getUserCode(), messageElement.getPayload().getData().get("phone"))
                            .doOnSuccess(el -> log.info(String.format("Phone number for user with code - %s was update", messageElement.getUserCode())));
                }).doOnError(error -> log.error(error.getMessage()));
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
