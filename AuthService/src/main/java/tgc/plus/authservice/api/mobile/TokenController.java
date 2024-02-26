package tgc.plus.authservice.api.mobile;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.api.mobile.utils.RequestsUtils;
import tgc.plus.authservice.dto.tokens_dto.TokensDataResponse;
import tgc.plus.authservice.dto.tokens_dto.UpdateToken;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.facades.TokenFacade;

@RestController
@RequestMapping("/tokens")
public class TokenController {

    @Autowired
    TokenFacade tokenFacade;

    @Autowired
    RequestsUtils requestsUtils;

    @PatchMapping("/update")
    public Mono<UpdateTokenResponse> updateToken(@RequestBody @Valid UpdateToken updateToken){
        return tokenFacade.updateAccessToken(updateToken);
    }

    @DeleteMapping("/token")
    public Mono<Void> deleteToken(@Pattern(regexp = "^ID-\\d+$") @RequestParam("tokenId") String tokenId){
        return tokenFacade.deleteToken(tokenId);
    }

    @DeleteMapping("/tokenAll")
    public Mono<Void> deleteAllTokens(@Pattern(regexp = "^ID-\\d+$") @RequestParam("currentTokenId") String tokenId){
        return tokenFacade.deleteAllTokens(tokenId);
    }

    @GetMapping("/tokenAll")
    public Mono<TokensDataResponse> getAllTokens(@Pattern(regexp = "^ID-\\d+$") @RequestParam("currentTokenId") String tokenId, ServerWebExchange serverWebExchange){
        return requestsUtils.getIpAddress(serverWebExchange).flatMap(ipAddress ->
                tokenFacade.getAllTokens(tokenId, ipAddress));
    }
}
