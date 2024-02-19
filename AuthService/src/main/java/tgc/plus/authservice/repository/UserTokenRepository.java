package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.sql.LockMode;
import org.springframework.data.relational.repository.Lock;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entity.UserToken;

import java.time.Instant;

@Repository
public interface UserTokenRepository extends ReactiveCrudRepository<UserToken, Long> {
    Mono<UserToken> getUserTokenByRefreshToken(String refreshToken);

    Mono<UserToken> getUserTokenByTokenId(String tokenId);

    Mono<Integer> removeUserTokenByTokenId(String tokenId);

    Mono<Integer> removeUserTokenByRefreshToken(String refreshToken);

    @Modifying
    @Query("UPDATE user_tokens SET refresh_token= :newRefreshToken, expired_date= :expiredDate, active_date= :activeDate WHERE refresh_token= :oldRefreshToken")
    Mono<Integer> updateRefreshToken(String oldRefreshToken, String newRefreshToken, Instant expiredDate, Instant activeDate);

    @Modifying
    @Query("DELETE FROM user_tokens WHERE user_id= :userId and token_id <> :tokenId")
    Mono<Integer> removeAllUserTokens(String tokenId, Long userId);


}
