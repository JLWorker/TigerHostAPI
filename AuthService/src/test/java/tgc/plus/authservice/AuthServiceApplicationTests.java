package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.ChangePhone;
import tgc.plus.authservice.dto.ChangeResponse;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.dto.user_dto.RestorePassword;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.dto.user_dto.UserLogin;

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
        String[] arrayList = {"ID-3019579106624", "ID-1243835269566"};
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
        Flux.range(0, 4)
                .flatMap(nm -> {
//                    if (nm==2) {
//                        System.out.println(nm);
//                        return sendRequestUpdateToken();
//                    }
//                    else {
                        System.out.println(nm);
                        return sendRequestDeleteToken()
                                .doOnSuccess(res -> logger.info("Success: " + nm))
                                .doOnError(e -> logger.warning(e.getMessage()));
//                    }
                }).subscribe();
    }

    private Mono<ChangeResponse> sendRequest(){
        return webClientRecover.put()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsInVzZXJfY29kZSI6ImVhNjI3YzM5LTAzZGItNDMxMy05ODAwLWFmODQzNzE4NGY0YiIsImV4cCI6MTcwODI1MTY5NywiaWF0IjoxNzA4MjUxMDk3fQ.dDOBpAhvJeYtGCyN0p_Ye9jb7ne0wEzvv2hmJb3D5ys")
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

    private Mono<Object> sendRequestDeleteAll(String id){
        WebClient webClientDelete = WebClient.create(String.format("http://localhost:8081/tokens/tokenAll?fromId=%s", id));
        return webClientDelete.delete()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiJlYTYyN2MzOS0wM2RiLTQzMTMtOTgwMC1hZjg0MzcxODRmNGIiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODMxOTc4NSwiaWF0IjoxNzA4MzE5MTg1fQ.uI4iqLDoE-f2IUqHUSYMtMyuupfK6hrtfxqvjioel3U")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(Object.class);


    }


    private Mono<Object> sendRequestDeleteToken(){
        WebClient webClientDelete = WebClient.create("http://localhost:8081/tokens/token?tokenId=ID-2965151262657");
        return webClientDelete.delete()
                .header("Authorization", "Bearer_eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2NvZGUiOiJlYTYyN2MzOS0wM2RiLTQzMTMtOTgwMC1hZjg0MzcxODRmNGIiLCJyb2xlIjoiVVNFUiIsImV4cCI6MTcwODMxOTQ2MCwiaWF0IjoxNzA4MzE4ODYwfQ.pF8YVS_aHmqxtCFH586PE0sU4bSlcXwL-0X8_QxdkAc")
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

}
