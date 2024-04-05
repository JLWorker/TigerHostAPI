package tgc.plus.providedservice.api;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.admin_api.*;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin")
public class AdminProvidedController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @PostMapping("/tariff")
    public Mono<Void> createTariff(@JsonView(NewTariff.Create.class) @Valid @RequestBody NewTariff newTariff) {
        return adminProvidedFacade.createElement(new VdsTariff(newTariff), VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
    }

    @PatchMapping("/tariff/{tariff_id}")
    public Mono<Void> changeTariff(@Valid @RequestBody @JsonView(NewTariff.Change.class) NewTariff newTariff, @PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.changeTariff(tariffId, newTariff);
    }

    @DeleteMapping("/tariff/{tariff_id}")
    public Mono<Void> deleteTariff(@PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.deleteElement(tariffId, VdsTariff.class);
    }

    @PatchMapping("/tariff/vision/{tariff_id}")
    public Mono<Void> changeTariffVision(@PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.changeVision(tariffId, VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
    }

    @GetMapping("/all_types")
    public Mono<CharacteristicsTypesResponse> getCharacteristicsTypes(){
        return adminProvidedFacade.getCharacteristicsTypes();
    }

    @GetMapping("/cpu_types")
    public Mono<List<CharacteristicType>> getCpuTypes(){
        return adminProvidedFacade.getCharacteristicType(CpuAbstractType.class);
    }


    @PostMapping("/cpu-type")
    public Mono<Void> createCpuType(@RequestBody @Valid NewCharacteristicType characteristicType){
        return adminProvidedFacade.createCharacteristicType(new CpuAbstractType(characteristicType), CpuAbstractType.class);
    }

    @PatchMapping("/cpu-type/{type_id}")
    public Mono<Void> changeCpuType(@RequestBody @Valid NewCharacteristicType characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, CpuAbstractType.class);
    }

    @DeleteMapping("/cpu-type/{type_id}")
    public Mono<Void> deleteCpuType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, CpuAbstractType.class);
    }

    @GetMapping("/ram_types")
    public Mono<List<CharacteristicType>> getRamTypes(){
        return adminProvidedFacade.getCharacteristicType(RamAbstractType.class);
    }
    @PostMapping("/ram-type")
    public Mono<Void> createRamType(@RequestBody @Valid NewCharacteristicType characteristicType){
        return adminProvidedFacade.createCharacteristicType(new RamAbstractType(characteristicType), RamAbstractType.class);
    }

    @PatchMapping("/ram-type/{type_id}")
    public Mono<Void> changeRamType(@RequestBody @Valid NewCharacteristicType characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, RamAbstractType.class);
    }

    @DeleteMapping("/ram-type/{type_id}")
    public Mono<Void> deleteRamType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, RamAbstractType.class);
    }

    @GetMapping("/mem_types")
    public Mono<List<CharacteristicType>> getMemoryTypes(){
        return adminProvidedFacade.getCharacteristicType(MemoryAbstractType.class);
    }

    @PostMapping("/mem-type")
    public Mono<Void> createMemoryType(@RequestBody @Valid NewCharacteristicType characteristicType){
        return adminProvidedFacade.createCharacteristicType(new MemoryAbstractType(characteristicType), MemoryAbstractType.class);
    }

    @PatchMapping("/mem-type/{type_id}")
    public Mono<Void> changeMemoryType(@RequestBody @Valid NewCharacteristicType characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, MemoryAbstractType.class);
    }

    @DeleteMapping("/mem-type/{type_id}")
    public Mono<Void> deleteMemoryType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, MemoryAbstractType.class);
    }

    @PostMapping("/period")
    public Mono<Void> createPeriod(@RequestBody @Valid NewPeriod newPeriod){
        return adminProvidedFacade.createElement(new Period(newPeriod), Period.class, EventTypesList.UPDATE_PERIODS);
    }

    @PatchMapping("/period/vision/{period_id}")
    public Mono<Void> changePeriodVision(@PathVariable(value = "period_id") Integer periodId) {
        return adminProvidedFacade.changeVision(periodId, Period.class, EventTypesList.UPDATE_PERIODS);
    }

    @DeleteMapping("/period/{period_id}")
    public Mono<Void> deletePeriod(@PathVariable(value = "period_id") Integer periodId) {
        return adminProvidedFacade.deleteElement(periodId, Period.class);
    }

    @PostMapping("/oc")
    public Mono<Void> createOperatingSystem(@RequestBody @Valid NewOperatingSystem newOperatingSystem){
        return adminProvidedFacade.createElement(new OperatingSystem(newOperatingSystem), OperatingSystem.class, EventTypesList.UPDATE_OC);
    }

    @PatchMapping("/oc/{oc_id}")
    public Mono<Void> changeOperatingSystem(@PathVariable(value = "oc_id") Integer ocId, @RequestBody @Valid @JsonView(NewOperatingSystem.Change.class) NewOperatingSystem newOperatingSystem) {
        return adminProvidedFacade.changeOperatingSystem(ocId, newOperatingSystem);
    }

    @DeleteMapping("/oc/{oc_id}")
    public Mono<Void> deleteOperatingSystem(@PathVariable(value = "oc_id") Integer ocId) {
        return adminProvidedFacade.deleteElement(ocId, OperatingSystem.class);
    }

}

