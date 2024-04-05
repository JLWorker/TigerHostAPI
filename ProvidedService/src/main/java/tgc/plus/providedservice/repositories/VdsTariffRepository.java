package tgc.plus.providedservice.repositories;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.entities.VdsTariff;

@Repository
public interface VdsTariffRepository extends ReactiveCrudRepository<VdsTariff, Integer> {

    @Query("SELECT * FROM vds_tariffs WHERE id= :tariffId FOR UPDATE")
    Mono<VdsTariff> getCallableBlockForVdsTariff(Integer tariffId);

}
