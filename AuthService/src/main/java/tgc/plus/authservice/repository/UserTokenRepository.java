package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.UserToken;

import java.time.Instant;

@Repository
public interface UserTokenRepository extends ReactiveCrudRepository<UserToken, Long> {
    Mono<UserToken> getUserTokenByRefreshToken(String refreshToken);

    Mono<Integer> removeUserTokenByRefreshToken(String refreshToken);

    Mono<Integer> removeUserTokenByTokenId(String tokenId);

    Mono<UserToken> getUserTokenByTokenId(String tokenId);

    @Modifying
    @Query("UPDATE user_tokens SET refresh_token= :newRefreshToken, expired_date= :expiredDate, create_date= :createDate WHERE refresh_token= :oldRefreshToken")
    Mono<Integer> updateRefreshToken(String oldRefreshToken, String newRefreshToken, Instant expiredDate, Instant createDate);

    @Modifying
    @Query("DELETE FROM user_tokens WHERE user_id= :userId and token_id <> :tokenId")
    Mono<Integer> removeAllUserTokens(String tokenId, Long userId);

    @Modifying
    @Query("SELECT * FROM user_tokens WHERE token_id= :tokenId FOR UPDATE")
    Mono<Void> getUserTokenByTokenIdForDeleteToken(String tokenId);

    @Query("SELECT * FROM user_tokens WHERE token_id= :tokenId FOR SHARE")
    Mono<UserToken> getUserTokenByTokenIdForShare(String tokenId);

    @Modifying
    @Query("DELETE FROM user_tokens WHERE expired_date < now()")
    Mono<Void> remoteUserTokenByTime();

}
