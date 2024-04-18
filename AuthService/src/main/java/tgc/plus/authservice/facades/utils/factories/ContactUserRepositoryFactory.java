package tgc.plus.authservice.facades.utils.factories;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.exceptions.exceptions_elements.CommandNotFoundException;
import tgc.plus.authservice.facades.utils.utils_enums.MailServiceCommand;
import tgc.plus.authservice.repository.UserRepository;

@Component
@Getter
public class ContactUserRepositoryFactory {

    @Autowired
    private UserRepository userRepository;

    public Mono<Long> execSqlQueryForContact(MailServiceCommand command, String contact, String userCode){
        return switch (command) {
            case UPDATE_EMAIL -> userRepository.changeEmail(contact, userCode);
            case UPDATE_PHONE -> userRepository.changePhone(contact, userCode);
            default -> Mono.error(new CommandNotFoundException(String.format("Command %s not found", command)));
        };
    }
}
