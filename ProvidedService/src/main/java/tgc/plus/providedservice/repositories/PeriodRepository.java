package tgc.plus.providedservice.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.entities.Period;

@Repository
public interface PeriodRepository extends ReactiveCrudRepository<Period, Integer> {

    @Query("select * from periods")
    Flux<Period> getAllPeriods();

    Mono<Period> getPeriodById(Integer id);

}
