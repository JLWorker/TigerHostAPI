package tgc.plus.callservice.repositories;

import jakarta.validation.constraints.Email;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.entity.User;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    Mono<User> getUserByUserCode(String userCode);

    @Modifying
    @Query("UPDATE user_communicate SET phone = :phone WHERE user_code = :userCode")
    Mono<Void> updatePhoneUser(String userCode, String phone);

    @Modifying
    @Query("UPDATE user_communicate SET email = :email WHERE user_code = :userCode")
    Mono<Void> updateEmailUser(String userCode, String email);

}
