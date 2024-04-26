package tgc.plus.authservice.facades;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.api.UserController;
import tgc.plus.authservice.dto.user_dto.*;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserFacadeTest {

    @Autowired
    private UserController userController;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiIwOGIyZDkxOS03NDQyLTQ5YjQtYjFkYi1lNWI3M2FmZDIwMGMiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcxMzcwOTE1NSwiaWF0IjoxNzEzNjQ5MTU1fQ.reo4eGdMgR2Fn4bT8Gy7zcw8l88LCCrKMNeBkWO6gdQ";

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081/api/account")
            .defaultHeader("Authorization", String.format("Bearer_%s", accessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();


    private final WebClient webClient2 = WebClient.builder()
            .baseUrl("http://localhost:8081/api/tokens")
            .defaultHeader("Authorization", String.format("Bearer_%s", accessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();

    @Test
    public void checkParallelMethods(){

        Flux<ResponseEntity<Void>> sendChangeEmailReq = Flux.range(0, 11).flatMap(num -> webClient.patch()
                .uri("/email")
                .bodyValue(new UserChangeContacts(null, "4357480@bk.ru"))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info(String.format("Success change mail, num - %s", num))));

        Mono<ResponseEntity<Void>> sendChangePhoneReq = webClient.patch()
                .uri("/phone")
                .bodyValue(new UserChangeContacts("89254380811", null))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success change phone!"));

        Mono<ResponseEntity<Void>> checkRecCode = webClient.patch()
                .uri("/check")
                .bodyValue(new RestorePassword("Qwerasd1234", "Qwerasd1234", "cc37a82b-620f-4765-8037-b9852f474cf3"))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success recovery!"));


//        List<String> tokens = List.of("f33715e5-2b19-40ad-ba3d-27c1e420fcb3", "914fb9f4-013e-487c-98d8-affbcdc5c2ce", "f8caad24-dce4-4de1-aa5a-b2aa3a205e8f");
//        Flux<UpdateTokenResponse> updateToken = Flux.range(0, 3).flatMap(num -> webClient2.patch()
//                .uri("/update")
//                .bodyValue(new UpdateToken(tokens.get(num)))
//                .retrieve()
//                .bodyToMono(UpdateTokenResponse.class)
//                .doOnError(e -> logger.error(e.getMessage()))
//                .doOnSuccess(res -> logger.info("Refresh token num {} was updated", res.getRefreshToken())));


        Flux<TokensResponse> loginUsers = Flux.range(0,3).flatMap(num ->
                webClient.post()
                .uri("/login")
                .header("Device-Ip", "31.220.2.54")
                .bodyValue(new UserLogin(new UserData("4357480@bk.ru", "Qazwsx1234", "Qazwsx1234"),
                        new DeviceData("Xiaomi", "mobile")))
                .retrieve()
                .bodyToMono(TokensResponse.class)
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info(res.getTokenId())));


//        checkRecCode.zipWith(sendChangePhoneReq).subscribe();
        sendChangeEmailReq.subscribe();

    }


}
