package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import tgc.plus.authservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
     Mono<User> getUserByUserCode(String userCode);
     Mono<User> getUserByEmail(String email);

     @Query("UPDATE users SET phone= :phone, version = version+1 WHERE user_code = :userCode and version = :reqVersion RETURNING version")
     Mono<Long> changePhone(String phone, String userCode, Long reqVersion);


     @Query("UPDATE users SET email= :email, version = version+1 WHERE user_code = :userCode and version = :reqVersion RETURNING version")
     Mono<Long> changeEmail(String email, String userCode, Long reqVersion);

     @Modifying
     @Query("UPDATE users SET recovery_code= :recoveryCode, code_expired_date= :expiredTime, version = version+1 " +
             "WHERE user_code = userCode and version = :reqVersion RETURNING version()")
     Mono<Long> changeRecoveryCode(String userCode, String recoveryCode, Instant expiredTime, Long version);

}
