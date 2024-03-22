package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import tgc.plus.proxmoxservice.entities.VdsPayment;

@Repository
public interface VdsPaymentRepository extends ReactiveCrudRepository<VdsPayment, Long> {
}
