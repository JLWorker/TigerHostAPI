package tgc.plus.authservice.api.mobile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorCode;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.facades.TwoFactorFacade;

@RestController
@RequestMapping("/2fa")
public class TwoAuthController {

    @Autowired
    TwoFactorFacade twoFactorFacade;

    @PatchMapping("/switch")
    public Mono<Void> checkTwoAuthCode(@RequestHeader("Version") Long version){
        return twoFactorFacade.switch2Fa(version);
    }

    @GetMapping("/qr")
    public Mono<QrCodeData> getQrCode(){
        return twoFactorFacade.getQrCode();
    }

    @PostMapping("/verify-code")
    public Mono<TokensResponse> verify2FaCode(@RequestBody @Valid TwoFactorCode twoFactorCode, @RequestHeader("2FA-Token") String token, ServerHttpRequest request){
        return twoFactorFacade.verifyCode(twoFactorCode, token, request.getHeaders().getFirst("Device-Ip"));
    }
}
