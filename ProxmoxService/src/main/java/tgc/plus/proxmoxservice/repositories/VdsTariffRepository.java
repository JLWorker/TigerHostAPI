package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tgc.plus.proxmoxservice.entities.VdsTariff;

public interface VdsTariffRepository extends ReactiveCrudRepository<VdsTariff, Long> {
}
