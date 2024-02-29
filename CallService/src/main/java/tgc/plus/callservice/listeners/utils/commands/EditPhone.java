package tgc.plus.callservice.listeners.utils.commands;

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
public class EditPhone implements Command {

    final UserRepository userRepository;
    public EditPhone(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Mono<Void> execution(MessageElement messageElement) {
        return userRepository.getUserByUserCode(messageElement.getUserCode())
                .defaultIfEmpty(new User())
                .filter(user -> user.getId()!=null)
                .switchIfEmpty(Mono.error(new UserNotFound(String.format("User with code - %s not found",  messageElement.getUserCode()))))
                .then(userRepository.updatePhoneUser(messageElement.getUserCode(), messageElement.getPayload().getData().get("phone")));

    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}
