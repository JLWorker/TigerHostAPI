package tgc.plus.callservice.listeners.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.dto.MessageData;
import tgc.plus.callservice.entity.User;
import tgc.plus.callservice.repositories.UserRepository;

import java.util.Objects;

@Component
@Slf4j
public class CheckTools {

    final
    UserRepository userRepository;

    public CheckTools(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<Boolean> checkUserByCode(String userCode) {
       return userRepository.getUserByUserCode(userCode).map(Objects::nonNull)
               .defaultIfEmpty(false);
    }

}