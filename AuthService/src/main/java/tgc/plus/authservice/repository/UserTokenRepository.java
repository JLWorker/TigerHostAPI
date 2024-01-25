package tgc.plus.authservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserTokenRepository extends ReactiveCrudRepository<tgc.plus.authservice.entity.UserToken, Long> {

//    public Mono<tgc.plus.authservice.entity.UserToken> getRefreshTokenByTokenId();

}
