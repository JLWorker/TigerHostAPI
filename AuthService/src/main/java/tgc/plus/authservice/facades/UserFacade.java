package tgc.plus.authservice.facades;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
//import tgc.plus.authservice.configs.R2Config;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.VersionsTypes;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;
import tgc.plus.authservice.dto.kafka_message_dto.PasswordRestoreData;
import tgc.plus.authservice.dto.user_dto.*;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.configs.utils.TokenProvider;
import tgc.plus.authservice.services.UserService;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserFacade {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    UserService userService;

    @Autowired
    TokenMetaService tokenMetaService;

    @Autowired
    FacadesUtils facadesUtils;

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public Mono<TokensResponse> registerUser(UserRegistration userRegistration, String ipAddr) {
        return Mono.defer(()->{
        UserData userData = userRegistration.getUserData();
        DeviceData deviceData = userRegistration.getDeviceData();
        if (userData.getPassword().equals(userData.getPasswordConfirm()))
            return facadesUtils.getUserByEmailReg(userData.getEmail())
                    .then(userService.save(userData).flatMap(savedUser -> tokenProvider.createAccessToken(savedUser.getUserCode(), savedUser.getRole())
                            .zipWith(tokenProvider.createRefToken(savedUser.getId()))
                            .flatMap(tokens -> tokenMetaService.save(deviceData, tokens.getT2().getId(), ipAddr)
                                    .flatMap(tokenMeta -> {
                                        log.info(String.format("User with userCode - %s, registered, tokens was created", savedUser.getUserCode()));
                                        return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(),
                                                Map.of(VersionsTypes.USER_VERSION.getName(), savedUser.getVersion(),
                                                        VersionsTypes.TOKEN_VERSION.getName(),tokenMeta.getVersion(),
                                                        VersionsTypes.DEVICE_VERSION.getName(),tokens.getT2().getVersion())));
                                    }))));
        else
            return Mono.error(new InvalidRequestException("Passwords mismatch"));
        }).doOnError(e -> log.error(e.getMessage()));
    }


    @Transactional
    public Mono<TokensResponse> loginUser(UserLogin userLogin, String ipAddr) {
        UserData userData = userLogin.getUserData();
        DeviceData deviceData = userLogin.getDeviceData();
        return facadesUtils.getUserByEmailLog(userData.getEmail())
                .flatMap(user -> facadesUtils.checkCredentials(user.getUserCode(), userData.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole())))
                        .then(tokenProvider.createAccessToken(user.getUserCode(), user.getRole())
                                    .zipWith(tokenProvider.createRefToken(user.getId()))
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
    public Mono<UserChangeContactResponse> changePhone(UserChangeContacts userChangeContacts) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
                String userCode = securityContext.getAuthentication().getPrincipal().toString();
                return facadesUtils.updatePhoneNumber(userChangeContacts.getPhone(), userCode, userChangeContacts.getVersion());
                }).doOnError(e->log.error(e.getMessage()));
    }

    @Transactional
    public Mono<UserChangeContactResponse> changeEmail(UserChangeContacts userChangeContacts) {
        return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
            String userCode = securityContext.getAuthentication().getPrincipal().toString();
            return facadesUtils.updateEmail(userChangeContacts.getEmail(), userCode, userChangeContacts.getVersion());
        }).doOnError(e->log.error(e.getMessage()));
    }

//    public Mono<UserInfoResponse> getInfoAboutAccount(){
//
//    }

//    @Transactional(transactionManager = "kafkaTransactionManager")
    public Mono<Void> generateRecoveryCode(RestorePassword restorePassword){
        return facadesUtils.getUserByEmailLog(restorePassword.getEmail())
                .flatMap(user -> facadesUtils.generateToken(user.getVersion(), user.getUserCode())
                        .flatMap(token -> facadesUtils.createMessageForRestorePsw(token, user.getUserCode())
                                .flatMap(message -> facadesUtils.sendMessageInCallService(message))))
                .doOnError(e -> log.error(e.getMessage()));
    }


}
//
//if (result!=null) {
//        log.info(result.getEmail());
//        return Mono.error(new InvalidRequestException(String.format("User with email %s already exist", userData.getEmail())));
//
//        }else {