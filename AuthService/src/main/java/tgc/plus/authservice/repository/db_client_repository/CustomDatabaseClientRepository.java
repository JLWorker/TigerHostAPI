package tgc.plus.authservice.repository.db_client_repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import tgc.plus.authservice.dto.database_dto.ChangeIpByTokenIdDTO;
import tgc.plus.authservice.dto.database_dto.UpdateTokensDTO;
import tgc.plus.authservice.dto.tokens_dto.TokenData;
import tgc.plus.authservice.entities.TokenMeta;
import tgc.plus.authservice.entities.UserToken;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class CustomDatabaseClientRepository {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private MappingR2dbcConverter mappingR2dbcConverter;

    public Mono<List<TokenData>> getAllUserTokens(Long userId){
        return databaseClient.sql(CustomRequests.GET_ALL_USER_TOKENS)
                .bindValues(Map.of("userId", userId))
                .map((row, metadata) -> mappingR2dbcConverter.read(TokenData.class, row))
                .all()
                .collectList();
    }

    public Mono<UpdateTokensDTO> getInfoForTokensUpdate(String refreshToken){
        return databaseClient.sql(CustomRequests.GET_INFO_FOR_UPDATE_TOKENS)
                .bind("refreshToken", refreshToken)
                .map((row, metadata) ->  mappingR2dbcConverter.read(UpdateTokensDTO.class, row))
                .first();
    }

    public Mono<ChangeIpByTokenIdDTO> getBlockTokenAndMeta(String tokenId){
        return databaseClient.sql(CustomRequests.GET_INFO_ABOUT_USER_TOKEN_AND_META)
                .bind("tokenId", tokenId)
                .map((row, metadata) -> mappingR2dbcConverter.read(ChangeIpByTokenIdDTO.class, row))
                .first();
    }


}
