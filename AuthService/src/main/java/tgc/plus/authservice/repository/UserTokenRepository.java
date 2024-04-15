package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.UserToken;

import java.time.Instant;

@Repository
public interface UserTokenRepository extends ReactiveCrudRepository<UserToken, Long> {
    Mono<UserToken> getUserTokenByRefreshToken(String refreshToken);

    Mono<Integer> removeUserTokenByRefreshToken(String refreshToken);

    Mono<Void> removeUserTokenByTokenId(String tokenId);

    Mono<UserToken> getUserTokenByTokenId(String tokenId);

    @Modifying
    @Query("UPDATE user_tokens SET refresh_token= :newRefreshToken, expired_date= :expiredDate, create_date= :createDate WHERE refresh_token= :oldRefreshToken")
    Mono<Integer> updateRefreshToken(String oldRefreshToken, String newRefreshToken, Instant expiredDate, Instant createDate);

    @Modifying
    @Query("DELETE FROM user_tokens WHERE user_id= :userId and token_id <> :tokenId")
    Mono<Integer> deleteUserTokensExceptTokenId(String tokenId, Long userId);


    @Query("SELECT * FROM user_tokens WHERE token_id= :tokenId FOR UPDATE")
    Mono<UserToken> getBlockForUserTokenById(String tokenId);

//    @Query("SELECT * FROM user_tokens WHERE token_id= :tokenId FOR SHARE")
//    Mono<UserToken> getShareBlockForTokensById(String tokenId);

    @Modifying
    @Query("DELETE FROM user_tokens WHERE expired_date < now()")
    Mono<Void> remoteUserTokenByTime();


    @Query("SELECT * FROM user_tokens JOIN users ON (user_tokens.user_id = users.id) WHERE users.user_code =:userCode FOR UPDATE")
    Flux<UserToken> getBlockForAllUserTokens(String userCode);

}
