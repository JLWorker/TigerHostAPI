package tgc.plus.proxmoxservice.listeners.utils.commands;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.ProxmoxMessage;
import tgc.plus.proxmoxservice.listeners.utils.Command;

@Component
public class CreateNewVm implements Command {
    @Override
    public Mono<Void> execution(ProxmoxMessage proxmoxMessage) {
        return null;
    }
}
