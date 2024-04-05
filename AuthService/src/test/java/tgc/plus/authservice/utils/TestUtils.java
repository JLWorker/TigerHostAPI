package tgc.plus.authservice.utils;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class TestUtils {

    public Mono<Map<String, String>> generateRandomUser(){
        String userCode = UUID.randomUUID().toString();
        int mail = new Random().nextInt();
        String email = String.format("%s@bk.ru", mail);
        String password = "12345";
        return Mono.just(Map.of("uc", userCode, "ps", password, "em", email));
    }

}
