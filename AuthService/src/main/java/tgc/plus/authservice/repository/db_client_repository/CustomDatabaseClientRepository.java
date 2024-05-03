package tgc.plus.authservice.repository.db_client_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.database_dto.ChangeIpByTokenIdDto;
import tgc.plus.authservice.dto.database_dto.UserInfoAndNumTokensDto;
import tgc.plus.authservice.dto.tokens_dto.TokenDataDto;

import java.util.List;
import java.util.Map;

@Repository
public class CustomDatabaseClientRepository {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private MappingR2dbcConverter mappingR2dbcConverter;

    public Mono<List<TokenDataDto>> getAllUserTokens(Long userId){
        return databaseClient.sql(CustomRequests.GET_ALL_USER_TOKENS)
                .bindValues(Map.of("userId", userId))
                .map((row, metadata) -> mappingR2dbcConverter.read(TokenDataDto.class, row))
                .all()
                .collectList();
    }

//    public Mono<UserInfoAndNumTokensDto> getUserInfoAndCountTokens(String email){
//        return databaseClient.sql(CustomRequests.GET_USER_AND_COUNT_TOKENS)
//                .bind("email", email)
//                .map((row, metadata) -> mappingR2dbcConverter.read(UserInfoAndNumTokensDto.class, row))
//                .first();
//    }

    public Mono<ChangeIpByTokenIdDto> getBlockTokenAndMeta(String tokenId){
        return databaseClient.sql(CustomRequests.GET_INFO_ABOUT_USER_TOKEN_AND_META)
                .bind("tokenId", tokenId)
                .map((row, metadata) -> mappingR2dbcConverter.read(ChangeIpByTokenIdDto.class, row))
                .first();
    }


}
