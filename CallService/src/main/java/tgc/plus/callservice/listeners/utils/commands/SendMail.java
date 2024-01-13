package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageData;
import tgc.plus.callservice.exceptions.UserNotFound;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
import tgc.plus.callservice.services.EmailSender;

@Component
@Slf4j
public class SendMail implements Command {

    private final EmailSender emailSender;

    private final UserRepository userRepository;

    public SendMail(EmailSender emailSender, UserRepository userRepository) {
        this.emailSender = emailSender;
        this.userRepository = userRepository;
    }

    @Override
    public void execution(MessageData messageData) {
        userRepository.getUserByUserCode(messageData.getUser_code())
                .doOnSuccess(user -> {
                    if(user!=null){
                        emailSender.sendMessage(messageData.getMessage(), user.getEmail());
                        log.info("Send request in emailSender");
                    }
                    else
                        throw new UserNotFound("User with code - " + messageData.getUser_code() + " not found");
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();

    }
}
