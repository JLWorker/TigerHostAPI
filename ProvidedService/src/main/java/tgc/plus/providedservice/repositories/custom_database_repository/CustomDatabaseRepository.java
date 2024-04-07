package tgc.plus.providedservice.repositories.custom_database_repository;

import io.r2dbc.spi.Connection;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.configs.R2Config;
import tgc.plus.providedservice.dto.api_dto.simple_api.TariffData;

import java.util.List;

@Repository
public class CustomDatabaseRepository {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private MappingR2dbcConverter mappingR2dbcConverter;

    public Mono<List<TariffData>> getTariffData(Integer hypervisorId){
            return databaseClient.sql(SqlRequestsList.GET_TARIFFS_WITH_TYPES.getQuery())
                    .bind("hypervisor_id", hypervisorId)
                    .map((row, rowMetadata) -> mappingR2dbcConverter.read(TariffData.class, row))
                    .all()
                    .collectList();
    }

    public Mono<TariffData> getTariffDataById(Integer id){
        return databaseClient.sql(SqlRequestsList.GET_TARIFF_WITH_TYPES_BY_ID.getQuery())
                .bind("tariffId", id)
                .map((row, rowMetadata)-> mappingR2dbcConverter.read(TariffData.class, row))
                .first();
    }

    public Mono<Boolean> getBlockForElement(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.GET_BLOCK_FOR_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .map(row -> row.get("active", Boolean.class))
                .one();
    }


    public <T> Mono<T> getCallableBlockForElement(Integer elemId, Class<T> targetClass){
        String table = targetClass.getAnnotation(Table.class).value();
        String sqlRequest = String.format(SqlRequestsList.GET_BLOCK_FOR_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .map((row, rowMetadata) -> mappingR2dbcConverter.read(targetClass, row))
                .first();

    }

    public Mono<Integer> getBlockForNonActiveElement(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.GET_BLOCK_FOR_NOT_ACTIVE_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .map(row -> row.get("id", Integer.class))
                .one();
    }


}
