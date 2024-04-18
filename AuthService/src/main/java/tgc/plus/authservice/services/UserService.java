package tgc.plus.authservice.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.entities.User;
import tgc.plus.authservice.entities.UserDetail;
import tgc.plus.authservice.exceptions.exceptions_elements.ServerException;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.utils.RoleList;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserService implements ReactiveUserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public Mono<User> save(UserData userData) {
        String userCode = UUID.randomUUID().toString();
        String password = bCryptPasswordEncoder.encode(userData.getPassword());
        return userRepository.save(new User(userCode, userData.getEmail(), password, RoleList.USER.name()));
    }
    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return userRepository.getUserByEmail(email)
                .filter(Objects::nonNull)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(String.format("User with email %s not exist", email))))
                .flatMap(user -> Mono.just(new UserDetail(List.of(new SimpleGrantedAuthority(user.getRole())), user.getUserCode(), user.getPassword(), user.getActive())));
    }
}
