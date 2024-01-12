package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageData;
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
    public void execution(MessageData messageData) {
        userRepository.updatePhoneUser(messageData.getUser_code(), messageData.getPhone())
                .doOnSuccess(el -> log.info("Phone number for user with code - " + messageData.getUser_code() + "was update"))
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();

    }
}
