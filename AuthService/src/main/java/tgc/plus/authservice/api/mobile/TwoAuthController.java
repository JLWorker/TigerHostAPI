package tgc.plus.authservice.api.mobile;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.api.mobile.utils.RequestsUtils;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorCode;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.facades.TwoFactorFacade;

@RestController
@RequestMapping("/2fa")
public class TwoAuthController {

    @Autowired
    TwoFactorFacade twoFactorFacade;

    @Autowired
    RequestsUtils requestsUtils;

    @PatchMapping("/switch")
    public Mono<Void> checkTwoAuthCode(@RequestHeader("Version") Long version){
        return twoFactorFacade.switch2Fa(version);
    }

    @GetMapping("/qr")
    public Mono<QrCodeData> getQrCode(){
        return twoFactorFacade.getQrCode();
    }

    @PostMapping("/verify-code")
    public Mono<TokensResponse> verify2FaCode(@RequestBody @Valid TwoFactorCode twoFactorCode, @RequestHeader("2FA-Token") String token, ServerWebExchange serverWebExchange){
        return requestsUtils.getIpAddress(serverWebExchange).flatMap(ipAddress -> twoFactorFacade.verifyCode(twoFactorCode, token, ipAddress));
    }
}
