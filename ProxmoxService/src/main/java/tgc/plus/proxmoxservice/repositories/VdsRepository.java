package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tgc.plus.proxmoxservice.entities.Vds;

public interface VdsRepository extends ReactiveCrudRepository<Vds, Long> {



}
