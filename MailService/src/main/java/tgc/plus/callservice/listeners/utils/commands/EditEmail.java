package tgc.plus.callservice.listeners.utils.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MailMessageDto;
import tgc.plus.callservice.dto.message_payloads.EditEmailPayloadDto;
import tgc.plus.callservice.entities.User;
import tgc.plus.callservice.exceptions.UserNotFoundException;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;

@Slf4j
@Component
public class EditEmail implements Command {

    final UserRepository userRepository;

    public EditEmail(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Mono<Void> execution(MailMessageDto mailMessageDto) {
        EditEmailPayloadDto editEmail = (EditEmailPayloadDto) mailMessageDto.getPayload();
        return userRepository.getUserByUserCodeForChange(mailMessageDto.getUserCode())
                .defaultIfEmpty(new User())
                .filter(user -> user.getId()!=null)
                .switchIfEmpty(Mono.error(new UserNotFoundException(String.format("User with code - %s not found", mailMessageDto.getUserCode()))))
                .then(userRepository.updateEmailUser(mailMessageDto.getUserCode(), editEmail.getEmail())
                                .doOnSuccess(success -> log.info(String.format("Email for user with code - %s was updated", mailMessageDto.getUserCode()))));
    }

    @Override
    public Mono<Void> executionForSender(String method, MailMessageDto mailMessageDto) {
        return Mono.empty();
    }

}
