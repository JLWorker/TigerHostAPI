package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
//import tgc.plus.authservice.configs.R2Config;
import tgc.plus.authservice.api.mobile.utils.IpValid;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.VersionsTypes;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.exceptions.exceptions_clases.TwoFactorActiveException;
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
@Validated
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
    public Mono<Void> registerUser(UserData userData) {
        return facadeUtils.checkPasswords(userData.getPassword(), userData.getPasswordConfirm())
                .then(facadeUtils.getUserByEmailReg(userData.getEmail()))
                    .then(userService.save(userData))
                            .flatMap(user -> facadeUtils.createMessageForSaveUser(user.getUserCode(), userData.getEmail(), userData.getPassword()))
                                    .flatMap(message -> facadeUtils.sendMessageInCallService(message, CommandsName.SAVE.getName()))
                .doOnError(e -> log.error(e.getMessage()));
    }


    @Transactional(noRollbackForClassName = "TwoFactorActiveException")
    public Mono<TokensResponse> loginUser(UserLogin userLogin, @IpValid String ipAddr) {
        UserData userData = userLogin.getUserData();
        DeviceData deviceData = userLogin.getDeviceData();
        return facadeUtils.getUserByEmailLog(userData.getEmail())
                .flatMap(user -> facadeUtils.checkCredentials(user.getUserCode(), userData.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())))
                        .then(Mono.defer(() -> {
                            if (user.getTwoAuthStatus())
                                return facadeUtils.generate2FaDeviceToken(user.getId())
                                        .flatMap(token -> Mono.error(new TwoFactorActiveException(token)));
                            else
                                return facadeUtils.generatePairTokens(user, deviceData, ipAddr);
                        })))
        .doOnError(e -> {
            if (!(e instanceof TwoFactorActiveException))
                log.error(e.getMessage());
        });

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

    public Mono<AuthRestorePasswordResponse> changePassword(RestorePassword restorePassword, Long version){
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadeUtils.checkPasswords(restorePassword.getPassword(), restorePassword.getPasswordConfirm())
                    .then(facadeUtils.simpleChangePassword(version, userCode, restorePassword.getPassword())
                            .flatMap(newVersion -> Mono.just(new AuthRestorePasswordResponse(newVersion))));
        }).doOnError(e -> log.error(e.getMessage()));
    }

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
                .then(facadeUtils.getUserByRecoveryCode(restorePassword.getCode())
                        .flatMap(user -> facadeUtils.changePasswordForRecovery(user.getVersion(), restorePassword.getPassword(), user.getCodeExpiredDate(), user.getUserCode())))
                .doOnError(e -> log.error(e.getMessage()));
    }

    @Transactional(readOnly = true)
    public Mono<UserInfoResponse> getInfoAboutAccount(Long version){
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
               String userCode =  securityContext.getAuthentication().getPrincipal().toString();
               return userRepository.getUserByUserCodeAndVersion(userCode, version).flatMap(user ->
                       Mono.just(new UserInfoResponse(user.getPhone(), user.getEmail(), user.getTwoAuthStatus())));
        });
    }


}