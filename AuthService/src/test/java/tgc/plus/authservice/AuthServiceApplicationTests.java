package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.ChangePhone;
import tgc.plus.authservice.dto.ChangeResponse;
import tgc.plus.authservice.dto.kafka_message_dto.PasswordRestoreData;
import tgc.plus.authservice.dto.user_dto.RestorePassword;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceApplicationTests {

    Logger logger = Logger.getLogger(this.getClass().getName());

//    private final WebClient webClient = WebClient.create("http://localhost:8081/account/phone");

    private final WebClient webClientRecover = WebClient.create("http://localhost:8081/account/check");

    @Test
    public void testTransactionals(){
        Flux<ChangeResponse> el = Flux.range(0,3)
                .flatMap(nm -> {
                    System.out.println(nm);
                    return sendRequestChangePswd()
                            .doOnSuccess(res -> logger.info(res.getVersion().toString() + "Success - " + nm.toString()))
                            .doOnError(e -> logger.warning(e.getMessage()));
                });

        el.subscribe();
    }
    private Mono<ChangeResponse> sendRequest(){
        return webClientRecover.put()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJfY29kZSI6IjBmYTZjNmJlLWIzODgtNDkzNy05ZmU1LThjNmUyZjBjNzEyMCIsImV4cCI6MTcwNzkzMjE2NSwiaWF0IjoxNzA3ODk5ODIzfQ.dozoUlKTeCJl5xfbWlvxyNJ6DgkjGpTFYSD4_CQzVH8")
                .body(Mono.just(new ChangePhone("89244272261", 13)), ChangePhone.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(ChangeResponse.class);


    }


    private Mono<ChangeResponse> sendRequestChangePswd(){
        return webClientRecover.put()
                .body(Mono.just(new RestorePassword("sgahTYjsx67", "sgahTYjsx67", "6748bf1a-f007-42ca-9e47-a63b2c199abc")), ChangePhone.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(ChangeResponse.class);


    }


}
