package tgc.plus.authservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import tgc.plus.authservice.entity.TokenMeta;

@Repository
public interface TokenMetaRepository extends ReactiveCrudRepository<TokenMeta, Long> {
}
