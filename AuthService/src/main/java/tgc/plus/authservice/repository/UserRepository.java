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

     Mono<User> getUserByEmail(String email);

     @Query("SELECT * FROM users WHERE user_code=:userCode AND two_factor_secret IS NOT NULL")
     Mono<User> getUserByUserCodeAndSecret(String userCode);

     @Query("SELECT * FROM users JOIN two_factor ON (users.id = two_factor.user_id) WHERE two_factor.device_token=:twoFactorToken")
     Mono<User> getUserByTwoFactorDeviceToken(String twoFactorToken);

     @Query("SELECT * FROM users WHERE user_code=:userCode FOR NO KEY UPDATE ")
     Mono<User> getBlockForUserByUserCode(String userCode);

     @Query("SELECT * FROM users WHERE recovery_code=:recoveryCode FOR NO KEY UPDATE ")
     Mono<User> getBlockForUserByRecoveryCode(String recoveryCode);

     @Query("SELECT * FROM users WHERE email=:email FOR NO KEY UPDATE ")
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
     Mono<Void> changePassword(String userCode, String password);

     @Modifying
     @Query("UPDATE users SET code_expired_date= null, recovery_code= null WHERE user_code= :userCode")
     Mono<Long> clearRecoveryCode(String userCode);

     @Modifying
     @Query("UPDATE users SET two_auth_status= :status WHERE user_code= :userCode")
     Mono<Long> switchTwoFactorStatus(String userCode, Boolean status);

     @Modifying
     @Query("UPDATE users SET two_auth_status= true, two_factor_secret= :secret WHERE user_code= :userCode")
     Mono<Void> updateTwoFactorSecret(String userCode, String secret);
}
