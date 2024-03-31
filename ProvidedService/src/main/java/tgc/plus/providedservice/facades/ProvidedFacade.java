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
import tgc.plus.providedservice.exceptions.facade_exceptions.ServerException;
import tgc.plus.providedservice.repositories.OperatingSystemRepository;
import tgc.plus.providedservice.repositories.PeriodRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseClient;

import java.util.Objects;

@Component
@Slf4j
public class ProvidedFacade {

    @Autowired
    private CustomDatabaseClient customDatabaseClient;

    @Autowired
    private OperatingSystemRepository operatingSystemRepository;

    @Autowired
    private PeriodRepository periodRepository;

    @Transactional
    public Mono<AllInfoResponse> getAllTariffsInfo(){
        return Mono.zip(customDatabaseClient.getTariffData(),
                operatingSystemRepository.getAllOperatingSystems().map(OcData::new).collectList(),
                periodRepository.getAllPeriods().map(PeriodData::new).collectList())
                .flatMap(result -> Mono.just(new AllInfoResponse(result.getT1(), result.getT3(), result.getT2())))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return getServerError();
                });
    }

    public Mono<TariffsInfo> getTariffsInfo(){
        return customDatabaseClient.getTariffData()
                .flatMap(tariffInfoData -> Mono.just(new TariffsInfo(tariffInfoData)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return getServerError();
                });
    }

    public Mono<PeriodsInfo> getPeriodsInfo(){
        return periodRepository.getAllPeriods()
                .map(PeriodData::new)
                .collectList()
                .flatMap(periodsInfo -> Mono.just(new PeriodsInfo(periodsInfo)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return getServerError();
                });
    }

    public Mono<OperatingSystemsInfo> getOperatingSystemsInfo(){
        return operatingSystemRepository.getAllOperatingSystems()
                .map(OcData::new)
                .collectList()
                .flatMap(operatingSystemsInfo -> Mono.just(new OperatingSystemsInfo(operatingSystemsInfo)))
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return getServerError();
                });
    }

    public Mono<TariffData> getTariffById(Integer tariffId){
        return customDatabaseClient.getTariffDataById(tariffId)
                .defaultIfEmpty(new TariffData())
                .filter(Objects::nonNull)
                .switchIfEmpty(getResourceNotFoundException(String.format("Tariff with id %s not found", tariffId)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return getServerError();
                    }
                    else
                        return Mono.error(e);

                });
    }

    public Mono<PeriodData> getPeriodById(Integer periodId){
        return periodRepository.getPeriodById(periodId)
                .defaultIfEmpty(new Period())
                .filter(Objects::nonNull)
                .switchIfEmpty(getResourceNotFoundException(String.format("Period with id %s not found", periodId)))
                .flatMap(period -> Mono.just(new PeriodData(period)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return getServerError();
                    }
                    else
                        return Mono.error(e);

                });
    }

    public Mono<OcData> getOperatingSystemById(Integer ocId){
        return operatingSystemRepository.getOperatingSystemById(ocId)
                .defaultIfEmpty(new OperatingSystem())
                .filter(Objects::nonNull)
                .switchIfEmpty(getResourceNotFoundException(String.format("Operating system with id %s not found", ocId)))
                .flatMap(oc -> Mono.just(new OcData(oc)))
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return getServerError();
                    }
                    else
                        return Mono.error(e);
                });
    }

    private <T> Mono<T> getServerError(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }

    private <T> Mono<T> getResourceNotFoundException(String message){
        return Mono.error(new ResourceNotFoundException(message));
    }

}
