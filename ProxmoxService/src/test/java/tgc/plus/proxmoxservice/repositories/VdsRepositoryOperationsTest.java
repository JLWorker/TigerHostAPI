package tgc.plus.proxmoxservice.repositories;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import tgc.plus.proxmoxservice.entities.Vds;
import tgc.plus.proxmoxservice.entities.VdsPayment;
import tgc.plus.proxmoxservice.entities.VdsTariff;
import tgc.plus.proxmoxservice.listeners.MessageListener;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VdsRepositoryOperationsTest {

    @Autowired
    private VdsRepository vdsRepository;

    @MockBean
    MessageListener messageListener;

    @Autowired
    private VdsPaymentRepository vdsPaymentRepository;

    @Autowired
    private VdsTariffRepository vdsTariffRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Random random = new Random();

    @Test
    public void fastVdsCreator(){
        Flux.range(0 ,5).flatMap(el -> {
            Vds randVds = new Vds();
            Instant startDate = Instant.now();
            randVds.setUserCode("4f17dc26-0171-4efc-a987-d1c4d5b8c9ea");
            randVds.setVmId(String.format("vm_%s", Instant.now().getNano()+random.nextInt(1000)));
            randVds.setStartDate(startDate);
            randVds.setVmNumber(179);
            randVds.setVmNode("tgc");
            randVds.setExpiredDate (startDate.plus(Duration.ofDays(10)));
            VdsPayment vdsPayment = new VdsPayment();
            vdsPayment.setPrice(random.nextInt(10000));
            vdsPayment.setPriceMonth(random.nextInt(1000));

            VdsTariff vdsTariff = new VdsTariff();
            vdsTariff.setTariffId(random.nextInt(10));
            vdsTariff.setOsId(random.nextInt(10));

            return vdsRepository.save(randVds)
                    .flatMap(vds -> {
                        vdsPayment.setVdsId(vds.getId());
                        vdsTariff.setVdsId(vds.getId());
                        return vdsPaymentRepository.save(vdsPayment)
                                .zipWith(vdsTariffRepository.save(vdsTariff));
                    });
        })
                .doOnError(e -> {
                    logger.error(e.getMessage());
                    Assertions.fail(e);
                })
                .doOnComplete(() -> logger.info("Success save"))
                .subscribe();
    }


}
