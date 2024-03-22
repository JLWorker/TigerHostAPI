package tgc.plus.proxmoxservice.repositories.db_client_repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.UserVmElemResponse;

import java.time.Instant;

@Repository
@Slf4j
public class CustomDatabaseClientRepository {

    @Autowired
    private DatabaseClient databaseClient;

    public Flux<UserVmElemResponse> getAllUserVms(String userCode){
        return databaseClient.sql(SqlList.GET_ALL_USER_VMS.getSqlRequest())
                .bind("userCode", userCode)
                .map(row->
                        new UserVmElemResponse(
                                row.get("vm_id", String.class),
                                row.get("expired_date", Instant.class),
                                row.get("start_date", Instant.class),
                                row.get("price_month", Integer.class),
                                row.get("price", Integer.class),
                                row.get("tariff_id", Integer.class),
                                row.get("period_id", Integer.class),
                                row.get("os_id", Integer.class),
                                row.get("active", Boolean.class))
                ).all();

    }




}
