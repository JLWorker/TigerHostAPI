package tgc.plus.proxmoxservice.listeners.utils;

import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.kafka_message_dto.KafkaProxmoxMessage;

public interface Command {
    Mono<Void> execution(KafkaProxmoxMessage proxmoxMessage);

}
