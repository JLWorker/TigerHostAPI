package tgc.plus.providedservice.api;

import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.simple_api.*;
import tgc.plus.providedservice.facades.ProvidedFacade;

@RestController
@RequestMapping("/api/provided")
@Validated
public class UserProvidedController {

    @Autowired
    private ProvidedFacade providedFacade;

    @GetMapping("/all")
    public Mono<AllInfoResponse> getAllInfo(){
        return providedFacade.getAllTariffsInfo();
    }

    @GetMapping("/tariffs")
    public Mono<TariffsInfo> getTariffsInfo(){
        return providedFacade.getTariffsInfo();
    }

    @GetMapping("/periods")
    public Mono<PeriodsInfo> getPeriodsInfo(){
        return providedFacade.getPeriodsInfo();
    }

    @GetMapping("/ocs")
    public Mono<OperatingSystemsInfo> getOperatingSystemsInfo(){
        return providedFacade.getOperatingSystemsInfo();
    }

    @GetMapping("/tariff/{id}")
    public Mono<TariffData> getTariffById(@Pattern(regexp = "\\d+") @PathVariable(value = "id") Integer id){
        return providedFacade.getTariffById(id);
    }

    @GetMapping("/period/{id}")
    public Mono<PeriodData> getPeriodById(@Pattern(regexp = "\\d+") @PathVariable(value = "id") Integer id){
        return providedFacade.getPeriodById(id);
    }

    @GetMapping("/oc/{id}")
    public Mono<OcData> getOperatingSystemById(@Pattern(regexp = "\\d+") @PathVariable(value = "id") Integer id){
        return providedFacade.getOperatingSystemById(id);
    }

}
