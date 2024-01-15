package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

@Slf4j
@Component
public class SavePhone implements Command {

    final
    UserRepository userRepository;

    public SavePhone(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void execution(MessageElement messageElement) {
        userRepository.updatePhoneUser(messageElement.getUser_code(), messageElement.getPayload().getData().get("phone"))
                .doOnSuccess(el -> log.info("Phone number for user with code - " + messageElement.getUser_code() + "was update"))
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();

    }

    @Override
    public void executionForSender(String method, MessageElement messageElement) {

    }

}
