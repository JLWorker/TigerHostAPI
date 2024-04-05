package tgc.plus.providedservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicType;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicsTypesResponse;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewOperatingSystem;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewTariff;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.exceptions.facade_exceptions.InvalidRequestException;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.facades.utils.EventTypesList;
import tgc.plus.providedservice.facades.utils.FacadesUtils;
import tgc.plus.providedservice.facades.utils.MessageAvailableList;
import tgc.plus.providedservice.repositories.CharacteristicRepository;
import tgc.plus.providedservice.repositories.OperatingSystemRepository;
import tgc.plus.providedservice.repositories.VdsTariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class AdminProvidedFacade {

    @Autowired
    private VdsTariffRepository vdsTariffRepository;

    @Autowired
    private OperatingSystemRepository operatingSystemRepository;

    @Autowired
    private FacadesUtils facadesUtils;

    @Autowired
    private CharacteristicRepository characteristicRepository;

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;


    @Transactional
    public <T extends TariffEntity> Mono<Void> createElement(T newElem, Class<T> classType, EventTypesList eventType){
        return customDatabaseRepository.saveElement(newElem, classType)
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
                        return Mono.error(e);
                    }
                });
    }

    @Transactional
    public <T> Mono<Void> changeVision(Integer elemId, Class<T> typeClass, EventTypesList eventType){
        String table = typeClass.getAnnotation(Table.class).value();
        return facadesUtils.getBlockForElements(elemId, VdsTariff.class)
                .then(customDatabaseRepository.changeActive(elemId, table))
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
    public <T> Mono<Void> deleteElement(Integer elemId, Class<T> classType){
        String table = classType.getAnnotation(Table.class).value();
        return customDatabaseRepository.getBlockForActiveElement(elemId, table)
                .filter(res -> res!=0)
                .switchIfEmpty(facadesUtils.getInvalidRequestException("Element not exist, or activity status must be turned off"))
                .then(customDatabaseRepository.deleteElement(elemId, table))
                .onErrorResume(e -> {
                    if(!(e instanceof InvalidRequestException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


    //необходимо создавать новый тариф
    @Transactional
    public Mono<Void> changeTariff(Integer tariffId, NewTariff newTariff){
        return vdsTariffRepository.getCallableBlockForVdsTariff(tariffId)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Tariff with id - %s not found", tariffId)))
                .flatMap(tariff -> {
                    tariff.setTariffName(newTariff.getTariffName());
                    tariff.setPriceMonthKop(newTariff.getPriceMonthKop());
                    tariff.setActive(newTariff.getActive());
                    tariff.setCpuType(newTariff.getCpuType());
                    tariff.setRamType(newTariff.getRamType());
                    tariff.setMemoryType(newTariff.getMemoryType());
                    return vdsTariffRepository.save(tariff);
                })
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_VDS_TARIFFS, MessageAvailableList.PUBLIC))
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
    public <T extends AbstractTypeEntity> Mono<Void> createCharacteristicType(T data, Class<T> entityClass){
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
    public <T extends AbstractTypeEntity> Mono<Void> changeCharacteristicType(String typeName, Integer typeId, Class<T> entityClass){
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
    public <T extends AbstractTypeEntity> Mono<Void> deleteCharacteristicType(Integer typeId, Class<T> entityClass){
         return facadesUtils.getBlockForElements(typeId, entityClass)
                .then(characteristicRepository.deleteCharacteristicType(typeId, entityClass))
                .onErrorResume(e -> {
                    if (e instanceof DataIntegrityViolationException)
                        return facadesUtils.getInvalidRequestException(String.format("Type with id - %s cannot remove, because it has connections with other tables", typeId));
                    else if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

    @Transactional(readOnly = true)
    public <T extends AbstractTypeEntity> Mono<List<CharacteristicType>> getCharacteristicType(Class<T> entityClass){
        return characteristicRepository.getCharacteristicTypes(entityClass)
                .map(CharacteristicType::new)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<CharacteristicsTypesResponse> getCharacteristicsTypes(){
        return Mono.zip(characteristicRepository.getCharacteristicTypes(CpuAbstractType.class).map(CharacteristicType::new).collectList(),
                characteristicRepository.getCharacteristicTypes(RamAbstractType.class).map(CharacteristicType::new).collectList(),
                characteristicRepository.getCharacteristicTypes(MemoryAbstractType.class).map(CharacteristicType::new).collectList())
                .flatMap(result -> Mono.just(new CharacteristicsTypesResponse(result.getT1(), result.getT2(), result.getT3())))
                .onErrorResume(e -> facadesUtils.getServerError());
    }


    @Transactional
    public Mono<Void> changeOperatingSystem(Integer ocId, NewOperatingSystem newOperatingSystem){
        return operatingSystemRepository.getCallableBlockForOperatingSystem(ocId)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Oc with id - %s not found", ocId)))
                .flatMap(oc -> {
                    oc.setOsName(newOperatingSystem.getName());
                    oc.setActive(newOperatingSystem.getActive());
                    oc.setBitDepth(newOperatingSystem.getBitDepth());
                    oc.setVersion(newOperatingSystem.getVersion());
                    oc.setPriceKop(newOperatingSystem.getPriceKop());
                    return operatingSystemRepository.save(oc);
                })
                .then(facadesUtils.sendMessageInFeedbackService(EventTypesList.UPDATE_OC, MessageAvailableList.PUBLIC))
                .onErrorResume(e -> {
                    if(!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }


}
