package tgc.plus.providedservice.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import tgc.plus.providedservice.entities.CpuType;

@Repository
public interface CpuTypeRepository extends ReactiveCrudRepository<CpuType, Integer> {
}
