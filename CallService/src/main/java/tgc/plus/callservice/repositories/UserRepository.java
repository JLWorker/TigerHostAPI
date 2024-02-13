package tgc.plus.callservice.repositories;

import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.callservice.configs.R2Config;
import tgc.plus.callservice.entity.User;

import static org.springframework.data.relational.core.query.Criteria.from;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Repository
public class UserRepository {

    private final R2dbcEntityTemplate template;

    public UserRepository(R2dbcEntityTemplate template) {
        this.template = template;
    }

    public Mono<User> saveUser(User user){
        return template.insert(User.class)
                .using(user);
    }

    public Mono<User> getUserByUserCode(String userCode){
        return template.select(User.class)
                .from("user_communicate")
                .matching(Query.query(where("user_code").is(userCode)))
                .one();
    }



//    Mono<User> getUserByUserCode(String userCode);
//
//    @Modifying
//    @Query("UPDATE user_communicate SET phone = :phone WHERE user_code = :userCode")
//    Mono<Void> updatePhoneUser(String userCode, String phone);
//
//    @Modifying
//    @Query("UPDATE user_communicate SET email = :email WHERE user_code = :userCode")
//    Mono<Void> updateEmailUser(String userCode, String email);

}
