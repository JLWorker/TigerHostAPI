package tgc.plus.providedservice.api;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;
import tgc.plus.providedservice.dto.api_dto.admin_api.TariffDto;
import tgc.plus.providedservice.entities.CpuType;
import tgc.plus.providedservice.entities.OperatingSystem;
import tgc.plus.providedservice.entities.VdsTariff;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());



}
