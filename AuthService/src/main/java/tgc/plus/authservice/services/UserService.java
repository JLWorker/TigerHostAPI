package tgc.plus.authservice.services;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.services.utils.RoleList;

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
        return userRepository.save(new User(userCode, userData.getEmail(), userData.getPassword(), RoleList.USER.getName()));
    }
}
