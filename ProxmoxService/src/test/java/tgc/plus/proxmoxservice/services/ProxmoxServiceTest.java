package tgc.plus.proxmoxservice.services;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxMergerNodeInfo;
import tgc.plus.proxmoxservice.listeners.MessageListener;

import java.time.Duration;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProxmoxServiceTest {

    @MockBean
    MessageListener messageListener;

    @Autowired
    private ProxmoxService proxmoxService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());



    @Test
    public void getUserInfo(){
         Flux.range(0, 6).delayElements(Duration.ofMillis(1000)).flatMap(el ->

             proxmoxService.getActualNodeInfo().take(1).map(res -> {
                 res.forEach(node -> logger.info("Name - {}, cpu - {}, ram - {}", node.getName(), node.getCpu(), node.getAvailRam()));
                 return Mono.empty();
             }).doOnError(e -> logger.info(e.getMessage()))).subscribe();

        try {
            Thread.sleep(11000);
            proxmoxService.getActualNodeInfo().take(1).map(res -> {
                res.forEach(node -> logger.info("Name - {}, cpu - {}, ram - {}" , node.getName(), node.getCpu(), node.getAvailRam()));
                return Mono.empty();
            }).doOnError(e -> logger.info(e.getMessage())).subscribe().dispose();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(14500);
            proxmoxService.getActualNodeInfo().take(1).map(res -> {
                res.forEach(node -> logger.info("Name - {}, cpu - {}, ram - {}" , node.getName(), node.getCpu(), node.getAvailRam()));
                return Mono.empty();
            }).doOnError(e -> logger.info(e.getMessage())).subscribe();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        try {
            Thread.sleep(17000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
