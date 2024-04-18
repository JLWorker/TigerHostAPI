package tgc.plus.authservice.services;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaims;
import tgc.plus.authservice.services.utils.RoleList;

import java.time.Instant;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Autowired
    private TokenService tokenService;

    @Test
    public void createAccessTest(){
        AccessTokenClaims accessTokenClaims = new AccessTokenClaims("hfysj87", "USER");
        tokenService.createAccessToken(accessTokenClaims, Instant.now().toString())
                .doOnSuccess(logger::info)
                .doOnError(e->logger.error(e.getMessage()))
                .subscribe();
    }

}
