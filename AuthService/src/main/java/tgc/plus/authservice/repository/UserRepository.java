package tgc.plus.authservice.repository;

import tgc.plus.authservice.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
     Mono<User> getUserById(int userId);

     Mono<User> getUserByEmail(String email);
}
