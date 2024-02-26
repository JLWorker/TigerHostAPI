package tgc.plus.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.TokenData;
import tgc.plus.authservice.repository.db_client_repository.CustomDatabaseClientRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthDbTests {

    @Autowired
    CustomDatabaseClientRepository customDatabaseClientRepository;

//    @Test
//    public void CheckSelectAll(){
//       Flux<TokenData> tokens = customDatabaseClientRepository.getAll(179L, "ID-7587965555568")
//               .flatMap(el -> {
//                   System.out.println(el.getTokenId() + " " + el.getTokenMetaData().getTokenDeviceIpAddress());
//                   return Mono.empty();
//               });
//       tokens.subscribe();
//    }

}
