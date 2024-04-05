package tgc.plus.callservice.listeners.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.configs.R2Config;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.exceptions.CommandNotFoundException;
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
public class CommandsDispatcher {
    private final Map<String, Command> commandMap = new HashMap<>();

    @Autowired
    EmailSender emailSender;

    @Autowired
    R2Config r2Config;

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    void init(){
        commandMap.put(CommandsNames.SAVE.getName(), new SaveUser(userRepository, emailSender));
        commandMap.put(CommandsNames.UPDATE_PHONE.getName(), new EditPhone(userRepository));
        commandMap.put(CommandsNames.UPDATE_EMAIL.getName(), new EditEmail(userRepository));
        commandMap.put(CommandsNames.SEND_EMAIL.getName(), new SendMail(emailSender, userRepository));
    }

    public Mono<Void> execute(String method, MessageElement messageElement){

        return Mono.defer(()-> {
            if (method.startsWith("send")) {
                return commandMap.get("send_em").executionForSender(method, messageElement);
            } else if (commandMap.containsKey(method)) {
                return commandMap.get(method).execution(messageElement);
            } else
               return Mono.error(new CommandNotFoundException(String.format("Command with name - %s not found " +
                       "::CommandsDispatcher", method)));
        }).onErrorResume(e -> {
            log.error("Error with message: {}, cause: {}", e.getMessage(), e.getCause());
            return Mono.empty();
        });
    }

}
