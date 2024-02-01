package tgc.plus.authservice.facades;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.VersionsTypes;
import tgc.plus.authservice.dto.user_dto.UserChangeContactResponse;
import tgc.plus.authservice.dto.user_dto.UserInfoResponse;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;
import tgc.plus.authservice.repository.UserRepository;

import java.util.Collection;
import java.util.Map;

@Component
@Slf4j
public class UserFacadeUtils {

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    @Autowired
    UserRepository userRepository;

    public Mono<Authentication> checkCredentials(String userCode, String password, Collection<GrantedAuthority> authority) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userCode, password, authority);
                return springSecurityConfig.reactiveLoginAuthenticationManager().authenticate(authenticationToken);
    }

    public Mono<Void> getUserByEmailReg(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() == null)
                .switchIfEmpty(getRequestException(String.format("User with email %s already exist", email)))
                .flatMap(el -> Mono.empty());
    }

    public Mono<User> getUserByEmailLog(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() != null)
                .switchIfEmpty(getRequestException(String.format("User with email %s not found", email)));
    }

    public Mono<UserChangeContactResponse> updatePhoneNumber(String phone, String userCode, Long reqVersion, User user){
          return userRepository.changePhone(phone, userCode, reqVersion)
                .filter(result -> !result.equals(0))
                .switchIfEmpty(getVersionException(user.getVersion()))
                .then(userRepository.getUserByUserCode(userCode)
                        .flatMap(userNewVersion -> {
                            log.info(String.format("Phone number was change for userCode %s, new number - %s", userCode, phone));
                            return Mono.just(new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), userNewVersion.getVersion(),
                                    "phone", phone)));
                }));
    }

    public Mono<UserChangeContactResponse> updateEmail(String email, String userCode, Long reqVersion, User user){
        return userRepository.changeEmail(email, userCode, reqVersion)
                .filter(result -> !result.equals(0))
                .switchIfEmpty(getVersionException(user.getVersion()))
                .then(userRepository.getUserByUserCode(userCode)
                        .flatMap(userNewVersion -> {
                            log.info(String.format("Email was change for userCode %s, new email - %s", userCode, email));
                            return Mono.just(new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), userNewVersion.getVersion(),
                                    "email", email)));
                        }));
    }

//    public Mono<UserInfoResponse> getInfo(String userCode, String regVersion){
//        return userRepository.getUserByUserCode(userCode)
//                .flatMap(user -> {
//                    if (!user.getVersion().equals(regVersion))
//                        return getVersionException(user.getVersion());
//                    else
//                        return Mono.just(new UserInfoResponse(user.getPhone(), user.getEmail(),
//                                user.getTwoAuthStatus(),user.getVersion()))
//                })
//    }

    private <T> Mono<T> getVersionException(Long version){
        return Mono.error(new VersionException(version, "Version incorrect"));
    }


    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

    private  <T> Mono<T> getUserNotFoundException(String message){
        return Mono.error(new UsernameNotFoundException(message));
    }

}
