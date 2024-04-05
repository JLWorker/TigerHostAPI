package tgc.plus.callservice.repositories;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.entities.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {

    Mono<User> getUserByUserCode(String userCode);

    @Query("SELECT * FROM user_communicate WHERE user_code= :userCode or email= :email FOR UPDATE")
    Mono<User> getUserByUserCodeForReg(String userCode, String email);

    @Query("SELECT * FROM user_communicate WHERE user_code= :userCode FOR UPDATE")
    Mono<User> getUserByUserCodeForChange(String userCode);

    @Modifying
    @Query("UPDATE user_communicate SET phone = :phone WHERE user_code = :userCode")
    Mono<Void> updatePhoneUser(String userCode, String phone);

    @Modifying
    @Query("UPDATE user_communicate SET email = :email WHERE user_code = :userCode")
    Mono<Void> updateEmailUser(String userCode, String email);


}
