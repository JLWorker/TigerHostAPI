package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageElement;
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
    public void execution(MessageElement messageElement) {
    }

    @Override
    public void executionForSender(String method, MessageElement messageElement) {
        userRepository.getUserByUserCode(messageElement.getUserCode())
                .doOnSuccess(user -> {
                    if(user!=null){
                        emailSender.sendMessage(messageElement.getPayload().getData(), user.getEmail(), method);
                    }
                    else
                        throw new UserNotFound("User with code - " + messageElement.getUserCode() + " not found");
                })
                .doOnError(error -> log.error(error.getMessage()))
                .subscribe();

    }

}
