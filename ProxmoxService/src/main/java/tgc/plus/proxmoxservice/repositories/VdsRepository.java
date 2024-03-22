package tgc.plus.proxmoxservice.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.entities.Vds;

@Repository
public interface VdsRepository extends ReactiveCrudRepository<Vds, Long> {

    Mono<Integer> getVdsByUserCode(String userCode);

    Mono<Vds> getVdsByUserCodeAndVmId(String userCode, String vmId);

}
