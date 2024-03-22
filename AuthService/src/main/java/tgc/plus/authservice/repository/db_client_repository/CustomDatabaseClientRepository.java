package tgc.plus.authservice.repository.db_client_repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.tokens_dto.TokenData;
import tgc.plus.authservice.dto.tokens_dto.TokenMetaData;

import java.util.List;
import java.util.Map;

@Repository
public class CustomDatabaseClientRepository {

    @Autowired
    DatabaseClient databaseClient;

    public Mono<List<TokenData>> getAllUserTokens(Long userId, String tokenId){
        return databaseClient.sql(SqlList.GET_ALL_USER_TOKENS.getSqlRequest())
                .bindValues(Map.of("userId", userId, "tokenId", tokenId))
                .map(row -> new TokenData(
                        (String) row.get("token_id"),
                        new TokenMetaData((String) row.get("name"), (String) row.get("type"), (String) row.get("ip"))
                ))
                .all()
                .collectList();
    }

}
