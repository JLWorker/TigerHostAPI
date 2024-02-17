package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
//import tgc.plus.authservice.configs.R2Config;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.VersionsTypes;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.facades.utils.CommandsName;
import tgc.plus.authservice.facades.utils.FacadeUtils;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserFacade {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserService userService;

    @Autowired
    TokenMetaService tokenMetaService;

    @Autowired
    FacadeUtils facadeUtils;

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    @Transactional
    public Mono<TokensResponse> registerUser(UserRegistration userRegistration, String ipAddr) {
        UserData userData = userRegistration.getUserData();
        DeviceData deviceData = userRegistration.getDeviceData();
        return facadeUtils.checkPasswords(userData.getPassword(), userData.getPasswordConfirm())
                .filter(res -> res).then(
                facadeUtils.getUserByEmailReg(userData.getEmail())
                    .then(userService.save(userData).flatMap(savedUser -> tokenService.createAccessToken(savedUser.getUserCode(), savedUser.getRole())
                            .zipWith(tokenService.createRefToken(savedUser.getId()))
                            .flatMap(tokens -> tokenMetaService.save(deviceData, tokens.getT2().getId(), ipAddr)
                                    .flatMap(tokenMeta -> facadeUtils.createMessageForSaveUser(savedUser.getUserCode(), savedUser.getEmail(), userData.getPassword())
                                                        .flatMap(msg -> facadeUtils.sendMessageInCallService(msg, CommandsName.SAVE.getName()))
                                                            .then(Mono.defer(() -> {
                                                                    log.info(String.format("User with userCode - %s, registered, tokens was created", savedUser.getUserCode()));
                                                                    return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(),
                                                                            Map.of(VersionsTypes.USER_VERSION.getName(), savedUser.getVersion(),
                                                                                    VersionsTypes.TOKEN_VERSION.getName(),tokenMeta.getVersion(),
                                                                                    VersionsTypes.DEVICE_VERSION.getName(),tokens.getT2().getVersion())));
                                                            }))
                                    ))))).doOnError(e -> log.error(e.getMessage()));
    }


    @Transactional
    public Mono<TokensResponse> loginUser(UserLogin userLogin, String ipAddr) {
        UserData userData = userLogin.getUserData();
        DeviceData deviceData = userLogin.getDeviceData();
        return facadeUtils.getUserByEmailLog(userData.getEmail())
                .flatMap(user -> facadeUtils.checkCredentials(user.getUserCode(), userData.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())))
                        .then(tokenService.createAccessToken(user.getUserCode(), user.getRole())
                                    .zipWith(tokenService.createRefToken(user.getId()))
                                    .flatMap(tokens -> tokenMetaService.save(deviceData, tokens.getT2().getId(), ipAddr)
                                            .flatMap(tokenMeta -> {
                                                log.info(String.format("User with email - %s, logged, tokens was created", user.getEmail() ));
                                                return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(),
                                                        Map.of(VersionsTypes.USER_VERSION.getName(), user.getVersion(),
                                                                VersionsTypes.TOKEN_VERSION.getName(),tokenMeta.getVersion(),
                                                                VersionsTypes.DEVICE_VERSION.getName(),tokens.getT2().getVersion())));
                                            })))).doOnError(e -> log.error(e.getMessage()));

    }

    @Transactional
    public Mono<UserChangeContactResponse> changePhone(UserChangeContacts userChangeContacts, Long version) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
                String userCode = securityContext.getAuthentication().getPrincipal().toString();
                return facadeUtils.updatePhoneNumber(userChangeContacts.getPhone(), userCode, version)
                        .flatMap(newVersion -> facadeUtils.createMessageForContactUpdate(userCode, userChangeContacts.getPhone(), UserChangeContacts.ChangePhone.class.getName())
                                .flatMap(message -> facadeUtils.sendMessageInCallService(message, CommandsName.UPDATE_PHONE.getName()))
                                    .thenReturn(new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), newVersion,
                                        "phone", userChangeContacts.getPhone()))));
                }).doOnError(e->log.error(e.getMessage()));

    }

    @Transactional
    public Mono<UserChangeContactResponse> changeEmail(UserChangeContacts userChangeContacts, Long version) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.updateEmail(userChangeContacts.getEmail(), userCode, version)
                    .flatMap(newVersion -> facadeUtils.createMessageForContactUpdate(userCode, userChangeContacts.getEmail(), UserChangeContacts.ChangeEmail.class.getName())
                         .flatMap(message -> facadeUtils.sendMessageInCallService(message, CommandsName.UPDATE_EMAIL.getName())
                                .thenReturn( new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), newVersion,
                                        "email", userChangeContacts.getEmail())))));
        }).doOnError(e->log.error(e.getMessage()));
    }


//    public Mono<UserInfoResponse> getInfoAboutAccount(){
//
//    }

    @Transactional
    public Mono<Void> generateRecoveryCode(RestorePassword restorePassword){
        return facadeUtils.getUserByEmailLog(restorePassword.getEmail())
                .flatMap(user -> facadeUtils.generateRecoveryCode(user.getVersion(), user.getUserCode())
                        .flatMap(token -> facadeUtils.createMessageForRestorePsw(token, user.getUserCode())
                                .flatMap(message -> facadeUtils.sendMessageInCallService(message, CommandsName.SEND_RECOVERY_CODE.getName()))))
                .doOnError(e -> log.error(e.getMessage()));
    }


    @Transactional(noRollbackForClassName = "RecoveryCodeExpiredException")
    public Mono<Void> checkRecoveryCode(RestorePassword restorePassword){
        return facadeUtils.checkPasswords(restorePassword.getPassword(), restorePassword.getPasswordConfirm())
                .filter(res -> res)
                .then(facadeUtils.getUserByRecoveryCode(restorePassword.getCode())
                        .flatMap(user -> facadeUtils.changePassword(user.getVersion(), restorePassword.getPassword(), user.getCodeExpiredDate(), user.getUserCode())))
                .doOnError(e -> log.error(e.getMessage()));
    }


}
//
//if (result!=null) {
//        log.info(result.getEmail());
//        return Mono.error(new InvalidRequestException(String.format("User with email %s already exist", userData.getEmail())));
//
//        }else {