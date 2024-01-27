package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
//import tgc.plus.authservice.configs.R2Config;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.dto.user_dto.UserRegistration;
import tgc.plus.authservice.dto.user_dto.RegistrationTokens;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

@Component
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

    @Transactional
    public Mono<RegistrationTokens> registerAccount(UserRegistration userRegistration, String ipAddr){
        UserData userData = userRegistration.getUserData();
        DeviceData deviceData = userRegistration.getDeviceData();
        if(userData.getPassword().equals(userData.getPasswordConfirm()))
         return  userRepository.getUserByEmail(userData.getEmail()).defaultIfEmpty(new User()).flatMap(result -> {
             if (result.getId()!=null) {
                return Mono.error(new InvalidRequestException(String.format("User with email %s already exist", userData.getEmail())));
            }
             else {
                 return userService.save(userData).flatMap(savedUser -> tokenService.createAccessToken(savedUser.getUserCode(), savedUser.getRole())
                         .zipWith(tokenService.createRefToken(savedUser.getId()))
                         .flatMap(tokens -> tokenMetaService.save(deviceData, tokens.getT2().getId(), ipAddr)
                                 .flatMap(tokenMeta -> Mono.just(new RegistrationTokens(tokens.getT1(), tokens.getT2().getRefreshToken(),
                                         savedUser.getVersion(), tokenMeta.getVersion(), tokens.getT2().getVersion())))));
             }});
        else
            return Mono.error(new InvalidRequestException("Passwords mismatch"));
    }
}
//
//if (result!=null) {
//        log.info(result.getEmail());
//        return Mono.error(new InvalidRequestException(String.format("User with email %s already exist", userData.getEmail())));
//
//        }else {