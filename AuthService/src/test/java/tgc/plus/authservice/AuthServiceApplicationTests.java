package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.ChangePhone;
import tgc.plus.authservice.dto.ChangeResponse;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceApplicationTests {

    Logger logger = Logger.getLogger(this.getClass().getName());

    private final WebClient webClient = WebClient.create("http://localhost:8081/account/phone");
    @Test
    public void testTransactionals(){
        Flux<ChangeResponse> el = Flux.range(0,3)
                .flatMap(nm -> {
                    System.out.println(nm);
                    return sendRequest()
                            .doOnSuccess(res -> logger.info(res.getVersion().toString() + "Success - " + nm.toString()))
                            .doOnError(e -> logger.warning(e.getMessage()));
                });

        el.subscribe();
    }
    private Mono<ChangeResponse> sendRequest(){
        return webClient.put()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiJlNjMwMmY1NC02MGJmLTQ3YWYtOGNlMi0xZTJjOTgxOTBjZjkiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwNjc5Mjc5NCwiaWF0IjoxNzA2NzYwNDUyfQ.eRr4qbaT-jq51Xwz7ZmfS3M6hBWd_pUVdHR27ui2ffw")
                .body(Mono.just(new ChangePhone("89244272269", 7)), ChangePhone.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(ChangeResponse.class);


    }

}
