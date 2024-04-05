package tgc.plus.providedservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.simple_api.*;
import tgc.plus.providedservice.entities.OperatingSystem;
import tgc.plus.providedservice.entities.Period;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.facades.utils.FacadesUtils;
import tgc.plus.providedservice.repositories.OperatingSystemRepository;
import tgc.plus.providedservice.repositories.PeriodRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.util.Objects;

@Component
@Slf4j
public class ProvidedFacade {

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;

    @Autowired
    private OperatingSystemRepository operatingSystemRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Autowired
    private FacadesUtils facadesUtils;

    @Transactional(readOnly = true)
    public Mono<AllInfoResponse> getAllTariffsInfo(){
        return Mono.zip(customDatabaseRepository.getTariffData(),
                operatingSystemRepository.getAllOperatingSystems().map(OcData::new).collectList(),
                periodRepository.getAllPeriods().map(PeriodData::new).collectList())
                .flatMap(result -> Mono.just(new AllInfoResponse(result.getT1(), result.getT3(), result.getT2())))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<TariffsInfo> getTariffsInfo(){
        return customDatabaseRepository.getTariffData()
                .flatMap(tariffInfoData -> Mono.just(new TariffsInfo(tariffInfoData)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<PeriodsInfo> getPeriodsInfo(){
        return periodRepository.getAllPeriods()
                .map(PeriodData::new)
                .collectList()
                .flatMap(periodsInfo -> Mono.just(new PeriodsInfo(periodsInfo)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<OperatingSystemsInfo> getOperatingSystemsInfo(){
        return operatingSystemRepository.getAllOperatingSystems()
                .map(OcData::new)
                .collectList()
                .flatMap(operatingSystemsInfo -> Mono.just(new OperatingSystemsInfo(operatingSystemsInfo)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public Mono<TariffData> getTariffById(Integer tariffId){
        return customDatabaseRepository.getTariffDataById(tariffId)
                .defaultIfEmpty(new TariffData())
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Tariff with id %s not found", tariffId)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);

                });
    }

    @Transactional(readOnly = true)
    public Mono<PeriodData> getPeriodById(Integer periodId){
        return periodRepository.getPeriodById(periodId)
                .defaultIfEmpty(new Period())
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Period with id %s not found", periodId)))
                .flatMap(period -> Mono.just(new PeriodData(period)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);

                });
    }

    @Transactional(readOnly = true)
    public Mono<OcData> getOperatingSystemById(Integer ocId){
        return operatingSystemRepository.getOperatingSystemById(ocId)
                .defaultIfEmpty(new OperatingSystem())
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Operating system with id %s not found", ocId)))
                .flatMap(oc -> Mono.just(new OcData(oc)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

}
