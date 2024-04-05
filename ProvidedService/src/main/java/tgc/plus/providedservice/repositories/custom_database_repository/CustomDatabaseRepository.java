package tgc.plus.providedservice.repositories.custom_database_repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.configs.R2Config;
import tgc.plus.providedservice.dto.api_dto.simple_api.TariffData;
import tgc.plus.providedservice.entities.TariffEntity;

import java.util.List;

@Repository
public class CustomDatabaseRepository {

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private R2Config r2Config;

    private R2dbcEntityTemplate template = null;

    @PostConstruct
    private void init(){
        template = r2Config.r2dbcEntityTemplate();
    }

    public Mono<List<TariffData>> getTariffData(){
            return databaseClient.sql(SqlRequestsList.GET_TARIFFS_WITH_TYPES.getQuery())
                    .map(row -> new TariffData(
                            row.get("tariff_id", Integer.class),
                            row.get("tariff_name", String.class),
                            row.get("price_month", Integer.class),
                            row.get("cpu", Integer.class),
                            row.get("ram", Integer.class),
                            row.get("memory", Integer.class),
                            row.get("cpu_type", String.class),
                            row.get("ram_type", String.class),
                            row.get("memory_type", String.class)
                    ))
                    .all()
                    .collectList();
    }

    public Mono<TariffData> getTariffDataById(Integer id){
        return databaseClient.sql(SqlRequestsList.GET_TARIFF_WITH_TYPES_BY_ID.getQuery())
                .bind("tariffId", id)
                .map(row -> new TariffData(
                        row.get("tariff_id", Integer.class),
                        row.get("tariff_name", String.class),
                        row.get("price_month", Integer.class),
                        row.get("cpu", Integer.class),
                        row.get("ram", Integer.class),
                        row.get("memory", Integer.class),
                        row.get("cpu_type", String.class),
                        row.get("ram_type", String.class),
                        row.get("memory_type", String.class)
                )).one();
    }

    public Mono<Integer> getBlockForElement(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.GET_BLOCK_FOR_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .map(row -> row.get("id", Integer.class))
                .one();
    }

    public Mono<Integer> getBlockForActiveElement(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.GET_BLOCK_FOR_NOT_ACTIVE_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .map(row -> row.get("id", Integer.class))
                .one();
    }

    public Mono<Void> changeActive(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.CHANGE_ACTIVE_STATUS.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .then();
    }


    public Mono<Void> deleteElement(Integer elemId, String table){
        String sqlRequest = String.format(SqlRequestsList.DELETE_ELEMENT.getQuery(), table);
        return databaseClient.sql(sqlRequest)
                .bind("elemId", elemId)
                .then();
    }

    public <T extends TariffEntity> Mono<Boolean> saveElement(T data, Class<T> typeClass){
        return template.insert(typeClass)
                .using(data)
                .flatMap(res->Mono.just(res.getActiveStatus()));

    }


}
