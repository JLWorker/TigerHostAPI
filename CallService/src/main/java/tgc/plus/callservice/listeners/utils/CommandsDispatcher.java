package tgc.plus.callservice.listeners.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tgc.plus.callservice.dto.MessageData;
import tgc.plus.callservice.listeners.utils.commands.SaveUser;
import tgc.plus.callservice.repositories.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommandsDispatcher {
    private final Map<String, Command> commandMap = new HashMap<>();

    @Autowired
    UserRepository userRepository;

    @PostConstruct
    void init(){
        commandMap.put(CommandsName.SAVE.getName(), new SaveUser(userRepository));
        commandMap.put(CommandsName.SAVE_PHONE.getName(), new SaveUser(userRepository));
    }

    public void execute(String method, MessageData messageData){
        try{
            if(commandMap.containsKey(method)){
                commandMap.get(method).execution(messageData);
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

}
