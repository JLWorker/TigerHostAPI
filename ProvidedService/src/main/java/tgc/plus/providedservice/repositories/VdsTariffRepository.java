package tgc.plus.providedservice.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import tgc.plus.providedservice.entities.Period;
import tgc.plus.providedservice.entities.VdsTariff;

@Repository
public interface VdsTariffRepository extends ReactiveCrudRepository<VdsTariff, Integer> {

}
