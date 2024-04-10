package tgc.plus.providedservice.repositories.test_utils;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.configs.R2Config;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.repositories.CharacteristicRepository;
import tgc.plus.providedservice.repositories.TariffRepository;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RemoverRepositoryTests {

    @Autowired
    private R2Config r2Config;

    private final  List<Class<? extends AbstractCharacteristicType>> charactersList = List.of(RamType.class, MemoryType.class, CpuType.class);

    @Test
    @Order(1)
    public void deleteTariffsTypes(){
        deleteAllByClassType(VdsTariff.class)
                .subscribe();
    }

    @Test
    @Order(2)
    public void deleteCharacteristicTypes(){
        Flux.fromIterable(charactersList)
                .flatMap(this::deleteAllByClassType)
                .subscribe();
    }

    @Test
    @Order(3)
    public void deleteHypervisors(){
        deleteAllByClassType(Hypervisor.class).subscribe();
    }

    @Test
    @Order(4)
    public void deleteOperatingSystems(){
        deleteAllByClassType(OperatingSystem.class).subscribe();
    }

    @Test
    @Order(5)
    public void deletePeriods(){
        deleteAllByClassType(Period.class).subscribe();
    }

    private <T> Mono<Void> deleteAllByClassType(Class<T> classType){
        return r2Config.r2dbcEntityTemplate().delete(classType)
                .all().then();
    }
}
