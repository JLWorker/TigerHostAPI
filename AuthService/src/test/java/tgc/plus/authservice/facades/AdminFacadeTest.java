package tgc.plus.authservice.facades;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.admin_dto.ChangeAccountDto;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.facades.utils.utils_enums.CookiePayload;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminFacadeTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final String adminAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiJmYzRjNzlhMC02MzU1LTQxMDQtOWQ4OS1hOGZjYzkzZjBmYTgiLCJyb2xlIjoiQURNSU4iLCJleHAiOjE3MTQyNDM3NzEsImlhdCI6MTcxNDE4Mzc3MX0.V7k6yIbFcex6UKVd6mnkapYprP6BuM0pRRu17j19wEs";
    private final String userAccessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiIyNjM1OTJlYi01ZmM0LTRiMDYtYjQyNS1mMTY3MGYyNWIzYTMiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcxNDI1NDczOSwiaWF0IjoxNzE0MTk0NzM5fQ.De2DLM31BeA7Fv0wluaTS8JAjj29N1C6RXnmH-b0rDs";

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081/api/auth/admin")
            .defaultHeader("Authorization", String.format("Bearer_%s", adminAccessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();


    private final WebClient webClient2 = WebClient.builder()
            .baseUrl("http://localhost:8081/api/auth/account")
            .defaultHeader("Authorization", String.format("Bearer_%s", userAccessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();

    private final WebClient webClient3 = WebClient.builder()
            .baseUrl("http://localhost:8081/api/auth/tokens")
            .defaultHeader("Authorization", String.format("Bearer_%s", userAccessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();

    @Test
    public void checkParallelMethods(){

        Flux<ResponseEntity<Void>> adminChangeAccount = Flux.range(0, 1).flatMap(num -> webClient.patch()
                .uri("/account")
                .bodyValue(new ChangeAccountDto("263592eb-5fc4-4b06-b425-f1670f25b3a3", false, "4357480@bk.ru", true, "ADMIN"))
                .retrieve()
                .toBodilessEntity()
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info(String.format("Success change user, num - %s", num))));

        Flux<ResponseEntity<Void>> adminRemoveTokens = Flux.range(0, 1).flatMap(num -> webClient.delete()
                .uri("/account/{userCode}/tokens", "263592eb-5fc4-4b06-b425-f1670f25b3a3")
                .retrieve()
                .toBodilessEntity()
                .doOnError(e->logger.info(String.format("Delete num %s error - %s", num, e.getMessage())))
                .doOnSuccess(res -> logger.info(String.format("Success delete user, num - %s", num))));

        List<String> tokens = List.of("ID-2275957388470", "ID-1380106055148");
        Flux<ResponseEntity<Void>> removeAllTokens = Flux.range(0, 2).flatMap(num ->
                            webClient3.delete()
                                    .uri("/all/{current_token}", tokens.get(num))
                                    .retrieve()
                                    .toBodilessEntity()
                                    .doOnError(e -> logger.error(e.getMessage()))
                                    .doOnSuccess(res -> logger.info("Success deleted!")));

        Mono<ResponseEntity<Void>> updateToken =  webClient3.patch()
                .uri("/update")
                .cookie(CookiePayload.REFRESH_TOKEN.name(), "26a74a0a-cdf8-474b-9b23-92e87fec59e5")
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success updated!"));

        Flux<TokensResponseDto> loginUsers = Flux.range(0,3).flatMap(num ->
                webClient2.post()
                .uri("/login")
                .header("Device-Ip", "31.220.2.54")
                .bodyValue(new UserLoginDto(new UserDataDto("4357480@bk.ru", "uyhYti78532", "uyhYti78532"),
                        new DeviceDataDto("Xiaomi", "mobile")))
                .retrieve()
                .bodyToMono(TokensResponseDto.class)
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info(res.getTokenId())));


        adminRemoveTokens.zipWith(updateToken).subscribe();

//        adminChangeAccount.subscribe();
////        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        loginUsers.subscribe();
//
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

    }


}
