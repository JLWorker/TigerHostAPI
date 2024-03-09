package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.PhoneChange;
import tgc.plus.authservice.dto.tokens_dto.TokensDataResponse;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.user_dto.*;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthServiceApplicationTests {

    Logger logger = Logger.getLogger(this.getClass().getName());

//    private final WebClient webClient = WebClient.create("http://localhost:8081/account/phone");

    private final WebClient webClientRecover = WebClient.create("http://localhost:8081/account/check");



//    @Test
//    public void testTransactionals(){
//        Flux<ResponseEntity> el = Flux.range(0,3)
//                .flatMap(nm -> {
//                    System.out.println(nm);
//                    return sendRequestChangePswd()
//                            .doOnSuccess(res -> logger.info(res.getVersion().toString() + "Success - " + nm.toString()))
//                            .doOnError(e -> logger.warning(e.getMessage()));
//                });
//
//        el.subscribe();
//    }


    @Test
    public void testDeleteAll(){
        String[] arrayList = {"ID-8539077008100", "ID-8620183119720"};
        Flux.fromArray(arrayList)
                .flatMap(nm -> {
                    System.out.println(nm);
                    return sendRequestDeleteAll(nm)
                            .doOnSuccess(res -> logger.info("Success: " + nm))
                            .doOnError(e -> logger.warning(e.getMessage()));
                }).subscribe();
    }

    @Test
    public void testIpAddr(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/account/login");
         webClientDelete.post().header("X-Forwarded-For", "192.168.68.103, 255.156.32.0")
                .body(Mono.just(new UserLogin(new UserData("4537480@bk.ru", "12345TYhj", "12345TYhj"), new DeviceData("Xiaomi", "web"))), UserLogin.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Object.class).subscribe();
    }

    @Test
    public void testDelete(){
        Flux.range(0, 3)
                .flatMap(nm -> {
//                    if (nm==0) {
//                        System.out.println(nm);
//                        return sendRequestToGetData();
//                    }
//                    else {
                        System.out.println(nm);
                        return sendRequestDeleteToken()
                                .doOnSuccess(res -> logger.info("Success: " + nm))
                                .doOnError(e -> logger.warning(e.getMessage()));
//                    }
                }).subscribe();
    }



    @Test
    public void testUserGetInfo(){
        Flux.range(0, 10)
                .flatMap(nm -> {
                    if (nm==6) {
                        System.out.println(nm);
                        return sendRequestToGetUserData();
                    }
                    else {
                    System.out.println(nm);
                    return sendRequestToChangePhone()
                            .doOnSuccess(res -> logger.info("Success: " + nm))
                            .doOnError(e -> logger.warning(e.getMessage()));
                    }
                }).subscribe();
    }


    private Mono<ResponseEntity<Void>> sendRequestToChangePhone(){
        WebClient patchPhone = WebClient.create("http://localhost:8081/account/phone");
        return patchPhone.patch()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiI1OWJhODI3ZS1hZjI5LTRlMTQtYTJjNS0zY2E5NmI2NmQyNWUiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODkzMTIyNCwiaWF0IjoxNzA4OTI4MDI0fQ.03MHRkCICawT_aVQdpsYNRmKV9cyxFizhDUTnOQT9-M")
                .header("Version", "18")
                .body(Mono.just(new PhoneChange("89244273135")), PhoneChange.class)
                .retrieve()
                .toBodilessEntity();


    }


//    private Mono<ChangeResponse> sendRequestChangePswd(){
//        return webClientRecover.put()
//                .body(Mono.just(new RestorePassword("sgahTYjsx67", "sgahTYjsx67", "6748bf1a-f007-42ca-9e47-a63b2c199abc")), ChangePhone.class)
//                .accept(MediaType.APPLICATION_JSON)
//                .retrieve().bodyToMono(ChangeResponse.class);
//
//
//    }

    private Mono<Object> sendRequestDeleteAll(String id){
        WebClient webClientDelete = WebClient.create(String.format("http://localhost:8081/tokens/tokenAll?currentTokenId=%s", id));
        return webClientDelete.delete()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJfY29kZSI6IjU1Mzc4ZGJhLWIyNjctNGNlYS1iNmFmLTFhZDdjMGU2YTBhNyIsImV4cCI6MTcwODkxNzM5OCwiaWF0IjoxNzA4OTE0MTk4fQ.iJ9LGCfS4wigqZRIOnpGQys0DeDEEfHXCSKXf1z9OOM")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Object.class);


    }


    private Mono<Object> sendRequestDeleteToken(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/tokens/token?tokenId=ID-3313995976820");
        return webClientDelete.delete()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiI1OWJhODI3ZS1hZjI5LTRlMTQtYTJjNS0zY2E5NmI2NmQyNWUiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODkyOTg1NiwiaWF0IjoxNzA4OTI2NjU2fQ.n7ohD2c42R-pHVr6spgPQbj1lYr2-5DARaREOloMBXg")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Object.class);


    }

    private Mono<Object> sendRequestUpdateToken(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/tokens/update");
        return webClientDelete.put()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiJlYTYyN2MzOS0wM2RiLTQzMTMtOTgwMC1hZjg0MzcxODRmNGIiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODMxODA2MCwiaWF0IjoxNzA4MzE3NDYwfQ.LVnlTyseHFNUd5uVA711fBARryISjqVp8tq3wNfKVro")
                .body(Mono.just(new UpdateToken("4a06b5aa-432e-4e9a-8ace-5c5c080917d8")), UpdateToken.class)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Object.class);


    }


    private Mono<Object> sendRequestToGetData(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/tokens/tokenAll?currentTokenId=ID-3313995976820");
        Mono<TokensDataResponse> tokensDataResponseMono = webClientDelete.get()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiI1OWJhODI3ZS1hZjI5LTRlMTQtYTJjNS0zY2E5NmI2NmQyNWUiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODkyOTg1NiwiaWF0IjoxNzA4OTI2NjU2fQ.n7ohD2c42R-pHVr6spgPQbj1lYr2-5DARaREOloMBXg")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(TokensDataResponse.class);

        return tokensDataResponseMono.flatMap(tokensDataResponse -> {
            System.out.println(tokensDataResponse.getTokenMetaData().getTokenDeviceName());
            return Mono.empty();
        });
    }


    private Mono<Object> sendRequestToGetUserData(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/account/info");
        Mono<UserInfoResponse> tokensDataResponseMono = webClientDelete.get()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiI1OWJhODI3ZS1hZjI5LTRlMTQtYTJjNS0zY2E5NmI2NmQyNWUiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODkzMTIyNCwiaWF0IjoxNzA4OTI4MDI0fQ.03MHRkCICawT_aVQdpsYNRmKV9cyxFizhDUTnOQT9-M")
                .header("Version", "18")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(UserInfoResponse.class);

        return tokensDataResponseMono.flatMap(userInfoResponse -> {
            System.out.println(userInfoResponse.getEmail() + " : " + userInfoResponse.getPhone());
            return Mono.empty();
        });
    }

}
