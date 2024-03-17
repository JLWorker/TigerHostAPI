package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import tgc.plus.proxmoxservice.entities.VdsPayment;

public interface VdsPaymentRepository extends ReactiveCrudRepository<VdsPayment, Long> {
}
