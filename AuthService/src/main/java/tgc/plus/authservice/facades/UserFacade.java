package tgc.plus.authservice.facades;

import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.dto.user_dto.UserRegistration;
import tgc.plus.authservice.dto.user_dto.RegistrationTokens;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.exceptions.UserExistException;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.UserService;

@Component
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
        return Mono.defer(()->{
            if (userData.getPassword().equals(userData.getPasswordConfirm())) {
                return userRepository.getUserByEmail(userData.getEmail()).flatMap(user -> {
                    if (user != null) {
                        return Mono.error(new UserExistException(String.format("User with email: %s exist", user.getEmail())));
                    } else
                        return userService.save(userData).flatMap(savedUser -> {
                             return tokenService.createAccessToken(savedUser.getUserCode(), savedUser.getRole()).zipWith(
                                    tokenService.createRefToken(savedUser.getId())).flatMap(tokens -> {
                                          return tokenMetaService.saveDeviceData(deviceData, tokens.getT2().getId(), ipAddr).flatMap(tokenMeta -> {
                                            return Mono.just(new RegistrationTokens(tokens.getT1(), tokens.getT2().getRefreshToken(),
                                                    user.getVersion(), tokenMeta.getVersion(), tokens.getT2().getVersion()));
                                        });
                            });
                        });
                });
            }
            else return Mono.error(new InvalidRequestException("Passwords mismatch"));
        });
    }

}
