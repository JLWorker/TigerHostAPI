package tgc.plus.providedservice.repositories.custom_database_repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.simple_api.TariffData;

import java.util.List;

@Repository
public class CustomDatabaseClient {

    @Autowired
    private DatabaseClient databaseClient;

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



}
