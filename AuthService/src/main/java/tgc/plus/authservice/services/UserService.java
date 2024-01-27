package tgc.plus.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.utils.RoleList;

import java.util.Base64;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }

    public Mono<User> save(UserData userData){
        String userCode = UUID.randomUUID().toString();
        String password = new BCryptPasswordEncoder().encode(userData.getPassword());
        return userRepository.save(new User(userCode, userData.getEmail(), password, RoleList.USER.getName()));
    }
}
