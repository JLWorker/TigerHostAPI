package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import tgc.plus.authservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
     Mono<User> getUserByUserCode(String userCode);
     Mono<User> getUserByEmail(String email);

     @Query("UPDATE users SET phone= :phone WHERE user_code = :userCode")
     Mono<User> changePhone(String phone, String userCode);
}
