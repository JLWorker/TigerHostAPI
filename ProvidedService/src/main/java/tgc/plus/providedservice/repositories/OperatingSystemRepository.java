package tgc.plus.providedservice.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.entities.OperatingSystem;

@Repository
public interface OperatingSystemRepository extends ReactiveCrudRepository<OperatingSystem, Integer> {

    @Query("SELECT * FROM operating_systems")
    Flux<OperatingSystem> getAllOperatingSystems();

    @Query("SELECT * FROM operating_systems WHERE id= :ocId FOR UPDATE")
    Mono<OperatingSystem> getCallableBlockForOperatingSystem(Integer ocId);

    Mono<OperatingSystem> getOperatingSystemById(Integer ocId);

}
