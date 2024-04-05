package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import tgc.plus.authservice.entities.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
     Mono<User> getUserByUserCode(String userCode);
     Mono<User> getUserByUserCodeAndVersion(String userCode, Long version);
     Mono<User> getUserByEmail(String email);
     Mono<User> getUserByRecoveryCode(String recoveryCode);
     Mono<User> getUserById(Long id);

     @Query("UPDATE users SET phone= :phone, version = version+1 WHERE user_code = :userCode and version = :reqVersion RETURNING version")
     Mono<Long> changePhone(String phone, String userCode, Long reqVersion);

     @Query("UPDATE users SET email= :email, version = version+1 WHERE user_code = :userCode and version = :reqVersion RETURNING version")
     Mono<Long> changeEmail(String email, String userCode, Long reqVersion);

     @Query("UPDATE users SET recovery_code= :recoveryCode, code_expired_date= :expiredTime, version = version+1 WHERE user_code= :userCode and version= :reqVersion RETURNING version")
     Mono<Long> changeRecoveryCode(String userCode, String recoveryCode, Instant expiredTime, Long reqVersion);

     @Query("UPDATE users SET password= :password, code_expired_date= null, recovery_code= null, version = version+1 WHERE user_code= :userCode and version= :reqVersion RETURNING version")
     Mono<Long> changePassword(String userCode, String password, Long reqVersion);

     @Query("UPDATE users SET code_expired_date= null, recovery_code= null, version = version+1 WHERE user_code= :userCode and version= :reqVersion RETURNING version")
     Mono<Long> clearRecoveryCode(String userCode, Long reqVersion);

     @Query("UPDATE users SET two_auth_status= :status, version = version+1 WHERE user_code= :userCode and version= :reqVersion RETURNING version")
     Mono<Long> switchTwoFactorStatus(String userCode, Boolean status, Long reqVersion);

     @Query("UPDATE users SET two_auth_status= true, two_factor_secret= :secret, version = version+1 WHERE user_code= :userCode and version= :reqVersion RETURNING version")
     Mono<Long> updateTwoFactorSecret(String userCode, String secret, Long reqVersion);
}
