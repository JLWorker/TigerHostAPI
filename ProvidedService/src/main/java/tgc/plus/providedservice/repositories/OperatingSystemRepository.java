package tgc.plus.providedservice.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.entities.OperatingSystem;

@Repository
public interface OperatingSystemRepository extends ReactiveCrudRepository<OperatingSystem, Integer> {

    @Query("select * from operating_systems")
    Flux<OperatingSystem> getAllOperatingSystems();

    Mono<OperatingSystem> getOperatingSystemById(Integer ocId);

}
