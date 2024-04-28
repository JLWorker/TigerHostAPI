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
import tgc.plus.authservice.dto.tokens_dto.TokenDataDto;
import tgc.plus.authservice.dto.user_dto.DeviceDataDto;
import tgc.plus.authservice.dto.user_dto.TokensResponseDto;
import tgc.plus.authservice.dto.user_dto.UserDataDto;
import tgc.plus.authservice.dto.user_dto.UserLoginDto;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenFacadeTest {

    private final String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiIzYTU1ZDQzYy0xNzU5LTQ1ZWMtYjg1OC1lMmI0M2RkMzlmMmEiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcxMzI0NTgyMCwiaWF0IjoxNzEzMTg1ODIwfQ.0Ld4aeu_t8O3Tw9mgPZBLrkCkZ8qBDzer-VH4br49JE";

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081/api/tokens")
            .defaultHeader("Authorization", String.format("Bearer_%s", accessToken))
            .clientConnector(new ReactorClientHttpConnector())
            .build();

    @Autowired
    private TokenFacade tokenFacade;

    @Autowired
    private UserFacade userFacade;





    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

//    @Test
//    public void testUpdate(){
//        UpdateToken updateToken = new UpdateToken("e3e28d10-e0fd-4536-8c21-d2081099aab8");
//        tokenFacade.updateAccessToken(updateToken)
//                .flatMap(updateTokenResponse -> {
//                    logger.info(String.format("Tokens: %s, %s", updateTokenResponse.getAccessToken(), updateTokenResponse.getRefreshToken()));
//                    return Mono.empty();
//                })
//                .doOnError(e ->logger.error(e.getMessage()))
//                .subscribe();
//
//    }

    @Test
    public void deleteAllTokens(){
        List<String> tokens = List.of("ID-2544834373027", "ID-1380106055148", "ID-6539091157185", "ID-7972283895082");
        Flux.range(0, 4).flatMap(num ->
             webClient.delete()
                    .uri("/all/{current_token}", tokens.get(num))
                              .retrieve()
                              .toBodilessEntity()
                     .doOnError(e -> logger.error(e.getMessage()))
                     .doOnSuccess(res -> logger.info("Success deleted!")))
                .subscribe();
    }

    @Test
    public void getAllTokens(){
        List<String> tokens = List.of("ID-1765450055906", "ID-4360584602219", "ID-5714680663874");
        Flux.range(0, 3).flatMap(num ->
                        webClient.get()
                                .uri("/all/{current_token}", tokens.get(num))
                                .header("Device-Ip", "21.223.121.5")
                                .retrieve()
                                .toEntityList(TokenDataDto.class)
                                .doOnError(e -> logger.error(e.getMessage()))
                                .doOnSuccess(res -> res.getBody().forEach(el -> logger.info(el.toString()))))
                .subscribe();
    }

    @Test
    public void testParallel(){

        String refreshToken = "c8af84b1-5f09-4415-aa08-249f98fcb160";
        String tokenId = "ID-8035414139640";
        String currentToken1 = "ID-2929587956976";
        String currentToken2 = "ID-3509808405351";


        Mono<TokensResponseDto> loginUser = webClient.mutate().baseUrl("http://localhost:8081/api/account").build()
                .post()
                .uri("/login")
                .header("Device-Ip", "31.220.2.54")
                .bodyValue(new UserLoginDto(new UserDataDto("4357480@bk.ru", "Qwerasd1234", "Qwerasd1234"),
                        new DeviceDataDto("Xiaomi", "mobile")))
                .retrieve()
                .bodyToMono(TokensResponseDto.class)
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info(res.getTokenId()));

//        Mono<UpdateTokenResponse> updateToken = webClient.patch()
//                .uri("/update")
//                .bodyValue(new UpdateToken(refreshToken))
//                .retrieve()
//                .bodyToMono(UpdateTokenResponse.class)
//                .doOnError(e -> logger.error(e.getMessage()))
//                .doOnSuccess(res -> logger.info(res.getRefreshToken()));

        Mono<ResponseEntity<Void>> deleteToken = webClient.delete()
                .uri("/token/{tokenId}", tokenId)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success delete!"));

        Mono<ResponseEntity<Void>> deleteAllTokens =  webClient.delete()
                .uri("/all/{currentTokenId}", currentToken2)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success deleted!"));

        Mono<ResponseEntity<List<TokenDataDto>>> getAllTokens =  webClient.get()
                .uri("/all/{currentTokenId}", currentToken1)
                .header("Device-Ip", "31.220.2.54")
                .retrieve()
                .toEntityList(TokenDataDto.class)
                .doOnError(e -> logger.error(e.getMessage()))
                .doOnSuccess(res ->
                    res.getBody().forEach(el -> logger.info(el.toString())));


        getAllTokens.zipWith(loginUser).subscribe();


    }


}
