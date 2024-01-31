package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;
import tgc.plus.authservice.repository.UserRepository;

import java.util.Collection;
import java.util.Objects;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

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

    public Mono<Void> getUserByCode(String userCode, Long dataVersion) {
        return userRepository.getUserByUserCode(userCode).defaultIfEmpty(new User())
                .filter(user -> user.getUserCode() != null)
                .switchIfEmpty(getUserNotFoundException(String.format("User with code %s not found", userCode)))
                .flatMap(user -> {
                    if (user.getVersion().equals(dataVersion))
                        return Mono.empty();
                    else
                        return getVersionException("Version incorrect", user.getVersion());
                });
    }

    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

    private <T> Mono<T> getUserNotFoundException(String message){
        return Mono.error(new UsernameNotFoundException(message));
    }

    private <T> Mono<T> getVersionException(String message, Long version){
        return Mono.error(new VersionException(version, message));
    }


}
