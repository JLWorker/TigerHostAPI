package tgc.plus.callservice.listeners.utils.commands;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tgc.plus.callservice.configs.R2Config;
import tgc.plus.callservice.dto.MessageElement;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.exceptions.UserAlreadyExist;
import tgc.plus.callservice.listeners.utils.Command;
import tgc.plus.callservice.repositories.UserRepository;
//import tgc.plus.callservice.services.CustomValidator;
import tgc.plus.callservice.services.EmailSender;

import java.util.Objects;


@Slf4j
@Component
public class SaveUser implements Command {

    final UserRepository userRepository;
    final EmailSender emailSender;

    final TransactionalOperator transactionalOperator;

    public SaveUser(UserRepository userRepository, EmailSender emailSender, TransactionalOperator transactionalOperator) {
        this.userRepository = userRepository;
        this.emailSender = emailSender;
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<Void> execution(MessageElement messageElement) {

      return userRepository.getUserByUserCode(messageElement.getUserCode())
               .defaultIfEmpty(new User())
               .filter(user -> user.getId()==null)
               .switchIfEmpty(Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", messageElement.getUserCode()))))
               .flatMap(user -> userRepository.save(new User(messageElement.getUserCode(), messageElement.getPayload().getData().get("email")))
                                    .map(userBd -> {
                                        log.info(String.format("User with code - %s was save", userBd.getUserCode()));
                                        return Mono.empty();
                //                                    return emailSender.sendMessage(messageElement.getPayload().getData(), userBd.getEmail(), "send_user_cr");
                                    }))
              .as(transactionalOperator::transactional)
              .doOnError(error -> log.error(error.getMessage())).then();
    }

    @Override
    public Mono<Void> executionForSender(String method, MessageElement messageElement) {
        return Mono.empty();
    }

}


// userRepository.getUserByUserCode(messageElement.getUserCode())
//        .defaultIfEmpty(new User())
//        .filter(user -> user.getId()!=null)
//        .switchIfEmpty(Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", messageElement.getUserCode()))))
//        .flatMap(user -> {
//        log.info(String.format("User with code - %s was save", user.getUserCode()));
//        return userRepository.save(new User(messageElement.getUserCode(), messageElement.getPayload().getData().get("email")))
//        .then(Mono.empty());
//        }),


//.map(Objects::nonNull)
// .defaultIfEmpty(false)
//                .flatMap(result -> {
//        if (result)
//            return Mono.error(new UserAlreadyExist(String.format("User with code - %s already exist", messageElement.getUserCode())));
//        else
//            return userRepository.save(new User(messageElement.getUserCode(), messageElement.getPayload().getData().get("email")))
//                    .map(userBd -> {
//                        log.info(String.format("User with code - %s was save", userBd.getUserCode()));
//                        return Mono.empty();
////                                    return emailSender.sendMessage(messageElement.getPayload().getData(), userBd.getEmail(), "send_user_cr");
//                    });
//    }).doOnError(error -> log.error(error.getMessage())).then();
