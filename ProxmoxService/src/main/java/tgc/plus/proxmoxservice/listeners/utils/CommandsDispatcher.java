package tgc.plus.proxmoxservice.listeners.utils;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.ProxmoxMessage;
import tgc.plus.proxmoxservice.exceptions.listener_exceptions.CommandNotFoundException;
import tgc.plus.proxmoxservice.listeners.utils.commands.CreateNewVm;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class CommandsDispatcher {
    private final Map<String, Command> commandMap = new HashMap<>();

    @PostConstruct
    void init(){
        commandMap.put(CommandsNames.CREATE_NEW_VM.getName(), new CreateNewVm());

    }
    public Mono<Void> execute(String method, ProxmoxMessage proxmoxMessage){

        return Mono.defer(()-> {
            if(commandMap.containsKey(method)){
                return commandMap.get(method).execution(proxmoxMessage);
            } else
               return Mono.error(new CommandNotFoundException(String.format("Command with name - %s not found ", method)));
        }).onErrorResume(e -> {
            log.error("Error with message: {}, cause: {}", e.getMessage(), e.getCause());
            return Mono.empty();
        });
    }

}
