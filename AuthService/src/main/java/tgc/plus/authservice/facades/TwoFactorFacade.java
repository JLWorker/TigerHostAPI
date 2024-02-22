package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorCode;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorSwitchResponse;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.exceptions.exceptions_clases.TwoFactorCodeException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.repository.TwoFactorRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.TwoFactorService;

@Service
@Slf4j
public class TwoFactorFacade {

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    TokenService tokenService;

    @Autowired
    TwoFactorService twoFactorService;

    @Autowired
    TwoFactorRepository twoFactorRepository;

    //вынести отправку версии в шлюз
    @Transactional
    public Mono<TwoFactorSwitchResponse> switch2Fa(Long version){
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.switch2FaStatus(userCode, version)
                    .doOnError(e -> log.error(e.getMessage()));
        });
    }

    public Mono<QrCodeData> getQrCode(){
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.generateQrCode(userCode)
                    .doOnError(e -> log.error(e.getMessage()));
        });
    }

    public Mono<TokensResponse> verifyCode(TwoFactorCode twoFactorCode, String token, String ipAddress){
        return tokenService.get2FaTokenData(token)
                .flatMap(deviceToken -> facadeUtils.verify2FaCode(deviceToken, twoFactorCode.getCode(), twoFactorCode.getDeviceData(), ipAddress))
                .then(Mono.just(new TokensResponse()))
                .doOnError(e -> log.error(e.getMessage()));

    }

}
