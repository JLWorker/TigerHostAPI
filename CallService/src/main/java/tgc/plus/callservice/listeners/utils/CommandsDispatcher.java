package tgc.plus.callservice.listeners.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageData;
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
public class CommandsDispatcher {
    private final Map<String, Command> commandMap = new HashMap<>();

    @Autowired
    UserRepository userRepository;

    @Autowired
    CheckTools checkTools;

    @Autowired
    EmailSender emailSender;

    @PostConstruct
    void init(){
        commandMap.put(CommandsName.SAVE.getName(), new SaveUser(userRepository, checkTools));
        commandMap.put(CommandsName.EDIT_PHONE.getName(), new EditPhone(userRepository, checkTools));
        commandMap.put(CommandsName.UPDATE_EMAIL.getName(), new EditEmail(userRepository, checkTools));
        commandMap.put(CommandsName.SEND_EMAIL.getName(), new SendMail(emailSender, userRepository));
    }

    public void execute(String method, MessageData messageData){
        try{
            if(commandMap.containsKey(method)){
                commandMap.get(method).execution(messageData);
            }
            else
                throw new CommandNotFound("Command with name - " + method + " not found");
        }
        catch (RuntimeException e){
            log.error(e.getMessage());
        }
    }

}
