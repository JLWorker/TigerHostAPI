package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import tgc.plus.authservice.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
     Mono<User> getUserByUserCode(String userCode);
     Mono<User> getUserById(Long id);

     Mono<User> getUserByEmail(String email);

     @Query("SELECT * FROM users WHERE user_code=:userCode FOR SHARE")
     Mono<User> getBlockForUserByUserCode(String userCode);

     @Query("SELECT * FROM users WHERE recovery_code=:recoveryCode FOR SHARE")
     Mono<User> getBlockForUserByRecoveryCode(String recoveryCode);

     @Query("SELECT * FROM users WHERE email=:email FOR SHARE")
     Mono<User> getBlockForUserByEmail(String email);

     @Modifying
     @Query("UPDATE users SET phone= :phone  WHERE user_code = :userCode")
     Mono<Long> changePhone(String phone, String userCode);

     @Modifying
     @Query("UPDATE users SET email= :email WHERE user_code = :userCode")
     Mono<Long> changeEmail(String email, String userCode);

     @Modifying
     @Query("UPDATE users SET recovery_code= :recoveryCode, code_expired_date= :expiredTime WHERE user_code= :userCode")
     Mono<Long> changeRecoveryCode(String userCode, String recoveryCode, Instant expiredTime);

     @Modifying
     @Query("UPDATE users SET password= :password, code_expired_date= null, recovery_code= null WHERE user_code= :userCode")
     Mono<Long> changePassword(String userCode, String password);

     @Modifying
     @Query("UPDATE users SET code_expired_date= null, recovery_code= null WHERE user_code= :userCode")
     Mono<Long> clearRecoveryCode(String userCode);

     @Modifying
     @Query("UPDATE users SET two_auth_status= :status WHERE user_code= :userCode")
     Mono<Long> switchTwoFactorStatus(String userCode, Boolean status, Long reqVersion);

     @Modifying
     @Query("UPDATE users SET two_auth_status= true, two_factor_secret= :secret WHERE user_code= :userCode")
     Mono<Long> updateTwoFactorSecret(String userCode, String secret, Long reqVersion);
}
