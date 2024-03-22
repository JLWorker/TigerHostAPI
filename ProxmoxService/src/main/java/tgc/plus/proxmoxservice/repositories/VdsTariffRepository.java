package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tgc.plus.proxmoxservice.entities.VdsTariff;

@Repository
public interface VdsTariffRepository extends ReactiveCrudRepository<VdsTariff, Long> {
}
