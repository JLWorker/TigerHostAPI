package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.TokenMeta;

@Repository
public interface TokenMetaRepository extends ReactiveCrudRepository<TokenMeta, Long> {

    Mono<TokenMeta> getTokenMetaByTokenId(Long tokenId);

    @Query("UPDATE token_meta SET device_ip= :ipAddress WHERE token_id= :tokenId RETURNING *")
    Mono<TokenMeta> updateIpAddress(String ipAddress, Long tokenId);

}
