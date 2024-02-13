package tgc.plus.callservice.listeners.utils.commands;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

import java.util.Objects;

@Slf4j
@Component
public class EditEmail implements Command {

    final UserRepository userRepository;

    public EditEmail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Mono<Void> execution(MessageElement messageElement) {
        return userRepository.getUserByUserCode(messageElement.getUserCode())
                .defaultIfEmpty(new User())
                .filter(user -> user.getId()!=null)
                .switchIfEmpty(Mono.error(new UserNotFound(String.format("User with code - %s not found", messageElement.getUserCode()))))
                .flatMap(result -> userRepository.updateEmailUser(messageElement.getUserCode(), messageElement.getPayload().getData().get("email"))
                                .doOnSuccess(success -> log.info(String.format("Email for user with code - %s was updated", messageElement.getUserCode()))))
                .doOnError(error -> log.error(error.getMessage()));
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
