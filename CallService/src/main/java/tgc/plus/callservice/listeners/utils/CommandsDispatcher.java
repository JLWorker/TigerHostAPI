package tgc.plus.callservice.listeners.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.CommandNotFound;
import tgc.plus.callservice.listeners.utils.commands.EditEmail;
import tgc.plus.callservice.listeners.utils.commands.EditPhone;
import tgc.plus.callservice.listeners.utils.commands.SaveUser;
import tgc.plus.callservice.listeners.utils.commands.SendMail;
import tgc.plus.callservice.repositories.UserRepository;
import tgc.plus.callservice.services.EmailSender;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Validated
public class CommandsDispatcher {
    private final Map<String, Command> commandMap = new HashMap<>();

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailSender emailSender;

    @PostConstruct
    void init(){
        commandMap.put(CommandsName.SAVE.getName(), new SaveUser(userRepository, emailSender));
        commandMap.put(CommandsName.EDIT_PHONE.getName(), new EditPhone(userRepository));
        commandMap.put(CommandsName.UPDATE_EMAIL.getName(), new EditEmail(userRepository));
        commandMap.put(CommandsName.SEND_EMAIL.getName(), new SendMail(emailSender, userRepository));
    }

    public Mono<Void> execute(String method, MessageElement messageElement){
            if (method.startsWith("send")) {
                return commandMap.get("send_em").executionForSender(method, messageElement);
            } else if (commandMap.containsKey(method)) {
                return commandMap.get(method).execution(messageElement);
            } else
                return Mono.error(new CommandNotFound(String.format("Command with name - %s not found ::CommandsDispatcher", method)));
        }
}
