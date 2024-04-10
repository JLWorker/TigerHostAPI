package tgc.plus.providedservice.repositories.test_utils;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.repositories.CharacteristicRepository;
import tgc.plus.providedservice.repositories.TariffRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreatorRepositoryTests {

    @Autowired
    private TariffRepository tariffRepository;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final Random random = new Random();

    private final List<Class<? extends AbstractCharacteristicType>> charactersList = List.of(RamType.class, MemoryType.class, CpuType.class);

    @Test
    @Order(1)
    public void createCharacteristicTypes() {
        Flux.fromIterable(charactersList)
                .flatMap(this::createCharacteristics)
                .subscribe();
    }

    @Test
    @Order(2)
    public void createHypervisors(){
        Flux.range(1, 3).flatMap(number -> {
            String hypervisorName = String.format("%s№%s", Hypervisor.class.getSimpleName(), number);
            Hypervisor hypervisor = new Hypervisor(hypervisorName);
            return tariffRepository.saveElem(hypervisor, Hypervisor.class);
        }).subscribe();
    }

    @Test
    @Order(3)
    public void createVdsTariffs() {
                Flux.zip(getAllIdCharacteristicType(RamType.class),
                                getAllIdCharacteristicType(CpuType.class),
                                getAllIdCharacteristicType(MemoryType.class),
                                tariffRepository.getInfoAboutAllRowsElem(Hypervisor.class).map(Hypervisor::getId).collectList())
                        .flatMap(result -> Flux.range(0, 10).flatMap(number -> {
                            String tariffName = String.format("%s№%s", VdsTariff.class.getSimpleName(), number);
                            return Mono.zip(getRandomFromList(result.getT2()),
                                    getRandomFromList(result.getT1()),
                                    getRandomFromList(result.getT3()),
                                            getRandomFromList(result.getT4()))
                                    .flatMap(randomElems -> {
                                        VdsTariff vdsTariff = new VdsTariff(tariffName,
                                                random.nextInt(10000, 100000),
                                                random.nextInt(1,10),
                                                random.nextInt(1,10),
                                                random.nextInt(1,10),
                                                randomElems.getT1(),
                                                randomElems.getT2(),
                                                randomElems.getT3(),
                                                randomElems.getT4());
                                        return tariffRepository.saveElem(vdsTariff, VdsTariff.class);
                                    });
                        })).subscribe();
    }

    @Test
    @Order(4)
    public void createPeriods() {
        Flux.range(1,6).flatMap(number -> {
            Period period = new Period(number*2, random.nextInt(0, 100));
            return tariffRepository.saveElem(period, Period.class);
        }).subscribe();
    }

    @Test
    @Order(5)
    public void createOperatingSystems(){
        Flux.range(1, 6).flatMap(number -> {
            String ocName = String.format("%s№%s", OperatingSystem.class.getSimpleName(), number);
            String version = String.format("v-%s", number);
            Integer templateId = Integer.valueOf(random.nextInt(10,20) + number.toString());
            OperatingSystem oc = new OperatingSystem(ocName, version, 64, templateId, random.nextInt(20000, 50000));
            return tariffRepository.saveElem(oc, OperatingSystem.class);
        }).subscribe();
    }



    private <T extends AbstractCharacteristicType> Mono<T> createCharacteristicType(Class<T> type, Integer number) {
        String typeName = String.format("%s№%s", type.getSimpleName(), number);
        try {
            return Mono.just(type.getDeclaredConstructor(String.class).newInstance(typeName));
        }
        catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return Mono.error(e);
        }
    }


    private <T extends AbstractCharacteristicType> Mono<Void> createCharacteristics(Class<T> type) {
        return Flux.range(0, 10)
                .flatMap(el -> createCharacteristicType(type, el)
                        .flatMap(newObj -> characteristicRepository.saveCharacteristicType(newObj, type)))
                .then()
                .onErrorResume(e -> {
                    logger.error(e.getMessage());
                    return Mono.error(e);
                });
    }

    private <T extends AbstractCharacteristicType> Mono<List<Integer>> getAllIdCharacteristicType(Class<T> type){
                return tariffRepository.getInfoAboutAllRowsElem(type)
                        .map(AbstractCharacteristicType::getId)
                .collectList()
                .filter(list -> !list.isEmpty())
                .switchIfEmpty(Mono.error(new RuntimeException("List is empty!")));
    }

    private Mono<Integer> getRandomFromList(List<Integer> list){
        return Mono.just(list.get(random.nextInt(0, list.size())));
    }

}
