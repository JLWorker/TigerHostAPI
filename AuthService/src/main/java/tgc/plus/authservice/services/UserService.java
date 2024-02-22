package tgc.plus.authservice.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.entity.UserDetail;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.utils.RoleList;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements ReactiveUserDetailsService {

    @Autowired
    UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public Mono<Void> save(UserData userData) {
        String userCode = UUID.randomUUID().toString();
        String password = bCryptPasswordEncoder.encode(userData.getPassword());
        return userRepository.save(new User(userCode, userData.getEmail(), password, RoleList.USER.name()))
                .then(Mono.empty());
    }

    @Override
    public Mono<UserDetails> findByUsername(String userCode) {
        return userRepository.getUserByUserCode(userCode).flatMap(user -> {
            if (user == null)
                return Mono.error(new UsernameNotFoundException(String.format("User with code %s not found", userCode)));
            else
                return Mono.just(new UserDetail(List.of(new SimpleGrantedAuthority(RoleList.USER.name())), user.getUserCode(),
                        user.getPassword(), user.getActive()));
        });
    }
}
