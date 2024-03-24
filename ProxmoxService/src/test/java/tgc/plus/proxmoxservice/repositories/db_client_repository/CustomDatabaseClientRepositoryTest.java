package tgc.plus.proxmoxservice.repositories.db_client_repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.api.VmControllerTest;
import tgc.plus.proxmoxservice.listeners.MessageListener;
import tgc.plus.proxmoxservice.repositories.VdsRepository;

import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomDatabaseClientRepositoryTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomDatabaseClientRepository customDatabaseClientRepository;

    @Autowired
    private VdsRepository vdsRepository;

    private final String userCode = "1dc93c33-db85-4724-b263-17eae46b1357";

    @MockBean
    MessageListener messageListener;

    @Test
    public void getAllUserVms() {
        AtomicInteger counter = new AtomicInteger();
            customDatabaseClientRepository.getAllUserVms(userCode)
                    .flatMap(userVmResponse -> {
                        logger.info("User VmId - {}", userVmResponse.getVmId());
                        counter.getAndIncrement();
                        return Mono.empty();
                    })
                    .doOnError(Assertions::fail)
                    .doOnComplete(() -> logger.info("Success, get - {} elements", counter))
                    .blockLast();
    }

}
