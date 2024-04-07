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
import tgc.plus.providedservice.exceptions.facade_exceptions.InvalidRequestException;
import tgc.plus.providedservice.exceptions.facade_exceptions.RelatedElementsException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.facades.utils.EventTypesList;
import tgc.plus.providedservice.facades.utils.FacadesUtils;
import tgc.plus.providedservice.facades.utils.MessageAvailableList;
import tgc.plus.providedservice.repositories.CharacteristicRepository;
import tgc.plus.providedservice.repositories.TariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.List;
import java.util.Objects;
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
                        return facadesUtils.getInvalidRequestException("Invalid types in body params");
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
    @Transactional
    public <T extends ProvidedServiceEntity> Mono<Void> deleteElement(Integer elemId, Class<T> classType){
        String table = classType.getAnnotation(Table.class).value();
        return customDatabaseRepository.getBlockForNonActiveElement(elemId, table)
                .filter(res -> res!=0)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException("Element not exist, or activity status must be turned off"))
                .then(tariffRepository.deleteTariffElem(elemId, classType))
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
        return tariffRepository.getHypervisorTariffs(hypervisorId)
                .filter(count -> count==0)
                .switchIfEmpty(facadesUtils.getRelatedElementsException("Elements cannot be deleted because related elements are present"))
                .then(deleteElement(hypervisorId, Hypervisor.class));
    }


    //необходимо создавать новый тариф
    @Transactional
    public Mono<Void> changeTariff(Integer tariffId, TariffDto tariffDto){
        return customDatabaseRepository.getCallableBlockForElement(tariffId, VdsTariff.class)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Tariff with id - %s not found", tariffId)))
                .flatMap(tariff -> {
                    tariff.setTariffName(tariffDto.getTariffName());
                    tariff.setPriceMonthKop(tariffDto.getPriceMonthKop());
                    tariff.setActive(tariffDto.getActive());
                    tariff.setCpuType(tariffDto.getCpuType());
                    tariff.setRamType(tariffDto.getRamType());
                    tariff.setMemoryType(tariffDto.getMemoryType());
                    return tariffRepository.updateElem(tariff);
                })
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_VDS_TARIFFS, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException)
                        return facadesUtils.getInvalidRequestException(String.format("Tariff with parameter - %s already exist", tariffDto.getTariffName()));
                    if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


    @Transactional
    public <T extends AbstractCharacteristicType> Mono<Void> createCharacteristicType(T data, Class<T> entityClass){
        return characteristicRepository.saveCharacteristicType(data, entityClass)
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException){
                        return facadesUtils.getInvalidRequestException(String.format("Type with name - %s already exist", data.getType()));
                    }
                    else {
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                });
    }

    @Transactional
    public <T extends AbstractCharacteristicType> Mono<Void> changeCharacteristicType(String typeName, Integer typeId, Class<T> entityClass){
        return facadesUtils.getBlockForElements(typeId, entityClass)
                .then(characteristicRepository.updateCharacteristicType(typeName, typeId, entityClass))
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_VDS_TARIFFS, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException){
                        return facadesUtils.getInvalidRequestException(String.format("Type with name - %s already exist", typeName));
                    }
                    else if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional //как и для всех delete перед вызовом данного endpoint стоит проверить машины, которые используют данный тип
    public <T extends AbstractCharacteristicType> Mono<Void> deleteCharacteristicType(Integer typeId, Class<T> entityClass){
         return facadesUtils.getBlockForElements(typeId, entityClass)
                .then(tariffRepository.deleteTariffElem(typeId, entityClass))
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
    public Mono<CharacteristicsTypesResponse> getCharacteristicsTypes(){
        return Mono.zip(characteristicRepository.getCharacteristicTypes(CpuType.class).map(CharacteristicTypeDto::new).collectList(),
                characteristicRepository.getCharacteristicTypes(RamType.class).map(CharacteristicTypeDto::new).collectList(),
                characteristicRepository.getCharacteristicTypes(MemoryType.class).map(CharacteristicTypeDto::new).collectList())
                .flatMap(result -> Mono.just(new CharacteristicsTypesResponse(result.getT1(), result.getT2(), result.getT3())))
                .onErrorResume(e -> facadesUtils.getServerError());
    }


    @Transactional
    public Mono<Void> changeOperatingSystem(Integer ocId, OperatingSystemDto operatingSystemDto){
        return customDatabaseRepository.getCallableBlockForElement(ocId, OperatingSystem.class)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Oc with id - %s not found", ocId)))
                .flatMap(oc -> {
                    oc.setOsName(operatingSystemDto.getName());
                    oc.setActive(operatingSystemDto.getActive());
                    oc.setBitDepth(operatingSystemDto.getBitDepth());
                    oc.setVersion(operatingSystemDto.getVersion());
                    oc.setPriceKop(operatingSystemDto.getPriceKop());
                    return tariffRepository.updateElem(oc);
                })
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_OC, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException)
                        return facadesUtils.getInvalidRequestException(String.format("Operating system with template - %s already exist", operatingSystemDto.getTemplateId()));
                    else if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


    @Transactional
    public Mono<Void> changeHypervisor(Integer hypervisorId, HypervisorDto hypervisorDto){
        return customDatabaseRepository.getCallableBlockForElement(hypervisorId, Hypervisor.class)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Hypervisor with id - %s not found", hypervisorId)))
                .flatMap(hypervisor -> {
                    hypervisor.setName(hypervisorDto.getName());
                    hypervisor.setActive(hypervisorDto.getActive());
                    return tariffRepository.updateElem(hypervisor);
                })
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_HYPERVISORS, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if (e instanceof DuplicateKeyException)
                        return facadesUtils.getInvalidRequestException(String.format("Hypervisor with name - %s already exist", hypervisorDto.getName()));
                    else if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


}
