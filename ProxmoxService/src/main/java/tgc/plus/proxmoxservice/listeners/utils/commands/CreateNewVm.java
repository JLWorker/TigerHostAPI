package tgc.plus.proxmoxservice.listeners.utils.commands;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.KafkaProxmoxMessage;
import tgc.plus.proxmoxservice.listeners.utils.Command;

@Component
public class CreateNewVm implements Command {
    @Override
    public Mono<Void> execution(KafkaProxmoxMessage proxmoxMessage) {
        return null;
    }
}
