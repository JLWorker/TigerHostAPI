package tgc.plus.providedservice.api;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewCharacteristicType;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewTariff;
import tgc.plus.providedservice.entities.CpuAbstractType;
import tgc.plus.providedservice.entities.VdsTariff;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Test
    void createTestTariff(){
        Flux.range(0, 1).flatMap(e -> {
            NewTariff newTariff = new NewTariff("name21", 400, 4, 12, 44, 1, 1, 1, false);
            return adminProvidedFacade.createElement(new VdsTariff(newTariff), VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
        }).subscribe();
    }

    @Test
    void changeVision(){
        Flux.range(0, 1).flatMap(e -> {
            logger.info("Create request for change vision, number {}", e);
            return adminProvidedFacade.changeVision(17, VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
        }).subscribe();
    }

    @Test
    void deleteElement(){
        Flux.range(0, 1).flatMap(e -> {
            logger.info("Create request for change vision, number {}", e);
            return adminProvidedFacade.deleteElement(17, VdsTariff.class);
        }).subscribe();
    }

    @Test
    void updateTariff(){
        NewTariff newTariff = new NewTariff("newTariff2112", 1401, 1, 1, 1, true);
//        NewTariff newTariff2 = new NewTariff("newTariff22", 1403, 4, 12, 44, 1, 1, 1, true);
//        NewTariff newTariff3 = new NewTariff("newTariff223", 1405, 4, 12, 44, 1, 1, 1, true);
        Flux.range(0, 1).flatMap(e -> {
//            if (e.equals(2)){
//                logger.info("Create request for change vision, number {}, change on Tariff2", e);
//                return adminProvidedFacade.changeTariff(15, newTariff2);
//            }
//            else if (e.equals(1)){
//                logger.info("Create request for change vision, number {}, change on Tariff3", e);
//                return adminProvidedFacade.changeTariff(15, newTariff3);
//            }
            logger.info("Create request for change vision, number {}, change on SimpleTariff", e);
            return adminProvidedFacade.changeTariff(15, newTariff);
        }).subscribe();
    }

    @Test
    void createCpuTypeTest(){
        Flux.range(0,1).flatMap(e -> {
            logger.info("Create request for create char. type number {}", e);
            NewCharacteristicType cpuType = new NewCharacteristicType("Ryzen-7");
            return adminProvidedFacade.createCharacteristicType(new CpuAbstractType(cpuType), CpuAbstractType.class);
        }).subscribe();
    }

    @Test
    void changeCpuTypeTest(){
        Flux.range(0,4).flatMap(e -> {
            logger.info("Create request for create char. type number {}", e);
            NewCharacteristicType cpuType = new NewCharacteristicType("Ryzen-8");
            NewCharacteristicType cpuType2 = new NewCharacteristicType("Ryzen-10");
            NewCharacteristicType cpuType3 = new NewCharacteristicType("Ryzen-12");
            if (e.equals(2)){
                return adminProvidedFacade.changeCharacteristicType(cpuType2.getTypeName(), 3, CpuAbstractType.class);
            }
            else if (e.equals(3)){
                return adminProvidedFacade.changeCharacteristicType(cpuType3.getTypeName(), 3, CpuAbstractType.class);
            }
            return adminProvidedFacade.changeCharacteristicType(cpuType.getTypeName(), 3, CpuAbstractType.class);
        }).subscribe();
    }


    @Test
    void deleteCpuTypeTest(){
        Flux.range(0, 3).flatMap(e -> {
            logger.info("Create request for create char. type number {}", e);
            return adminProvidedFacade.deleteCharacteristicType(2, CpuAbstractType.class);
        }).subscribe();
    }


}
