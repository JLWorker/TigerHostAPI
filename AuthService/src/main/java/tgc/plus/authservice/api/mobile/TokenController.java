package tgc.plus.authservice.api.mobile;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.facades.TokenFacade;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @Autowired
    TokenFacade tokenFacade;

    @PutMapping("/update")
    public Mono<UpdateTokenResponse> updateToken(@RequestBody @Valid UpdateToken updateToken){
        return tokenFacade.updateAccessToken(updateToken);
    }

    @DeleteMapping("/token")
    public Mono<Void> deleteToken(@RequestParam("tokenId") String tokenId){
        return tokenFacade.deleteToken(tokenId);
    }

    @DeleteMapping("/tokenAll")
    public Mono<Void> deleteAllTokens(@RequestParam("fromId") String tokenId){
        return tokenFacade.deleteAllToken(tokenId);
    }

}
