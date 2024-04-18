package tgc.plus.authservice.facades;

import dev.samstevens.totp.exceptions.QrGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.exceptions.exceptions_elements.QrCodeGeneratorException;
import tgc.plus.authservice.facades.utils.FacadeUtils;
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

//    @Transactional
//    public Mono<Void> switch2Fa(Long version){
//        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
//            String userCode = securityContext.getAuthentication().getPrincipal().toString();
//            return facadeUtils.switch2FaStatus(userCode, version)
//                    .then(facadeUtils.createMessageForUpdateUserInfo(EventsTypesNames.UPDATE_ACCOUNT.getName())
//                            .flatMap(feedbackMessage -> facadeUtils.sendMessageInFeedbackService(feedbackMessage, userCode)))
//                    .doOnError(e -> log.error(e.getMessage()));
//        });
//    }

    @Transactional(readOnly = true)
    public Mono<QrCodeData> getQrCode(){
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.generateQrCode(userCode)
                    .onErrorResume(e -> {
                        log.error(e.getMessage());
                        if (e instanceof QrGenerationException){
                            return Mono.error(new QrCodeGeneratorException("The server cannot generate QR code"));
                        }
                        else return Mono.error(e);
                    });
        });
    }

//    @Transactional
//    public Mono<TokensResponse> verifyCode(TwoFactorCode twoFactorCode, String token, String ipAddress){
//        return tokenService.get2FaTokenData(token)
//                .flatMap(deviceToken -> facadeUtils.verify2FaCode(deviceToken, twoFactorCode.getCode(), twoFactorCode.getDeviceData(), ipAddress))
//                .doOnError(e -> log.error(e.getMessage()));
//    }

}
