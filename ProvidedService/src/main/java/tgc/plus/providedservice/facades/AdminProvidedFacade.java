package tgc.plus.providedservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.admin_api.*;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.facades.utils.EventTypesList;
import tgc.plus.providedservice.facades.utils.FacadesUtils;
import tgc.plus.providedservice.facades.utils.MessageAvailableList;
import tgc.plus.providedservice.repositories.CharacteristicRepository;
import tgc.plus.providedservice.repositories.TariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.List;
import java.util.function.Function;

@Component
@Slf4j
public class AdminProvidedFacade {

    @Autowired
    private FacadesUtils facadesUtils;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;

    @Autowired
    private TariffRepository tariffRepository;


    @Transactional
    public <T extends ProvidedServiceEntity> Mono<Void> createElement(T newElem, Class<T> classType, EventTypesList eventType){
        return tariffRepository.saveElem(newElem, classType)
                .filter(res -> res)
                .switchIfEmpty(Mono.empty())
                .then(facadesUtils.sendMessageInFeedbackService(eventType, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException)
                        return facadesUtils.getInvalidRequestException(String.format("Element with parameter - %s already exist", newElem.getUniqueElement()));
                    else if (e instanceof DataIntegrityViolationException)
                        return facadesUtils.getInvalidRequestException("No dependencies found for parameters");
                    else {
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                });
    }

    @Transactional(readOnly = true)
    public <T extends ProvidedServiceEntity, R> Mono<List<R>> getAllRowsElement(Class<T> classType, Function<T, R> converter){
        return tariffRepository.getInfoAboutAllRowsElem(classType)
                .map(converter)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });

    }

    @Transactional
    public <T extends ProvidedServiceEntity> Mono<Void> changeVision(Integer elemId, Class<T> typeClass, EventTypesList eventType){
        return facadesUtils.getBlockForElements(elemId, typeClass)
                .flatMap(status -> tariffRepository.changeActive(elemId, status, typeClass))
                .then(facadesUtils.sendMessageInFeedbackService(eventType, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


    //перед удалением нужна проверка на то, что элемент не реализуется в виртуальных машинах!!!
    @Transactional()
    public <T extends ProvidedServiceEntity> Mono<Void> deleteElement(Integer elemId, Class<T> classType){
        String table = classType.getAnnotation(Table.class).value();
        return customDatabaseRepository.getBlockForNonActiveElement(elemId, table)
                .filter(res -> res!=0)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException("Element not exist, or activity status must be turned off"))
                .then(tariffRepository.deleteElem(elemId, classType))
                .onErrorResume(e -> {
                    if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> deleteHypervisor(Integer hypervisorId){
        return tariffRepository.getTariffsByHypervisor(hypervisorId)
                .filter(count -> count==0)
                .switchIfEmpty(facadesUtils.getRelatedElementsException("Elements cannot be deleted because related elements are present"))
                .then(deleteElement(hypervisorId, Hypervisor.class));
    }


    //необходимо создавать новый тариф
    @Transactional
    public Mono<Void> changeTariff(Integer tariffId, ChangeTariffDto tariffDto){
            return facadesUtils.changeElement(tariffId, VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS, (tariff) -> {
                tariff.setTariffName(tariffDto.getTariffName());
                tariff.setPriceMonthKop(tariffDto.getPriceMonthKop());
                tariff.setActive(tariffDto.getActive());
                tariff.setCpuType(tariffDto.getCpuType());
                tariff.setRamType(tariffDto.getRamType());
                tariff.setMemoryType(tariffDto.getMemoryType());
                return tariff;
            });
    }

    @Transactional
    public Mono<Void> changeOperatingSystem(Integer ocId, ChangeOperatingSystemDto operatingSystemDto){
        return facadesUtils.changeElement(ocId, OperatingSystem.class, EventTypesList.UPDATE_OC, (oc) -> {
                    oc.setOsName(operatingSystemDto.getName());
                    oc.setActive(operatingSystemDto.getActive());
                    oc.setBitDepth(operatingSystemDto.getBitDepth());
                    oc.setVersion(operatingSystemDto.getVersion());
                    oc.setPriceKop(operatingSystemDto.getPriceKop());
                    return oc;
                });
    }

    @Transactional
    public Mono<Void> changePeriod(Integer periodId, ChangePeriodDto periodsDto){
        return facadesUtils.changeElement(periodId, Period.class, EventTypesList.UPDATE_PERIODS, (period) -> {
            period.setDiscountPercent(periodsDto.getDiscountPercent());
            period.setActive(periodsDto.getActive());
            return period;
        });
    }

    @Transactional
    public <T extends AbstractCharacteristicType> Mono<Void> changeCharacteristicType(CharacteristicTypeDto characteristicTypeDto, Integer typeId, Class<T> entityClass){
        return facadesUtils.changeElement(typeId, entityClass, EventTypesList.UPDATE_VDS_TARIFFS, (type) -> {
            type.setTypeName(characteristicTypeDto.getTypeName());
            return type;
        });
    }


    @Transactional
    public Mono<Void> changeHypervisor(Integer hypervisorId, HypervisorDto hypervisorDto){
        return facadesUtils.changeElement(hypervisorId, Hypervisor.class, EventTypesList.UPDATE_HYPERVISORS, (hypervisor) -> {
                    hypervisor.setName(hypervisorDto.getName());
                    hypervisor.setActive(hypervisorDto.getActive());
                    return hypervisor;
                });
    }


    @Transactional
    public <T extends AbstractCharacteristicType> Mono<Void> createCharacteristicType(T data, Class<T> entityClass){
        return characteristicRepository.saveCharacteristicType(data, entityClass)
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException){
                        return facadesUtils.getInvalidRequestException(String.format("Type with name - %s already exist", data.getTypeName()));
                    }
                    else {
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                });
    }

    @Transactional //как и для всех delete перед вызовом данного endpoint стоит проверить машины, которые используют данный тип
    public <T extends AbstractCharacteristicType> Mono<Void> deleteCharacteristicType(Integer typeId, Class<T> entityClass){
        return facadesUtils.getCallableBlockForElements(typeId, entityClass)
                .then(tariffRepository.deleteElem(typeId, entityClass))
                .onErrorResume(e -> {
                    if (e instanceof DataIntegrityViolationException)
                        return facadesUtils.getRelatedElementsException(String.format("Type with id - %s cannot remove, because it has connections with other tables", typeId));
                    else if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional(readOnly = true)
    public <T extends AbstractCharacteristicType> Mono<List<CharacteristicTypeDto>> getCharacteristicType(Class<T> entityClass){
        return characteristicRepository.getCharacteristicTypes(entityClass)
                .map(CharacteristicTypeDto::new)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<CharacteristicsTypesResponseDto> getCharacteristicsTypes(){
        return Mono.zip(characteristicRepository.getCharacteristicTypes(CpuType.class).map(CharacteristicTypeDto::new).collectList(),
                        characteristicRepository.getCharacteristicTypes(RamType.class).map(CharacteristicTypeDto::new).collectList(),
                        characteristicRepository.getCharacteristicTypes(MemoryType.class).map(CharacteristicTypeDto::new).collectList())
                .flatMap(result -> Mono.just(new CharacteristicsTypesResponseDto(result.getT1(), result.getT2(), result.getT3())))
                .onErrorResume(e -> facadesUtils.getServerError());
    }



}
