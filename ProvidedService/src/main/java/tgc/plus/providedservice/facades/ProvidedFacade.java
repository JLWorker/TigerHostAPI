package tgc.plus.providedservice.facades;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.simple_api.*;
import tgc.plus.providedservice.entities.OperatingSystem;
import tgc.plus.providedservice.entities.Period;
import tgc.plus.providedservice.entities.ProvidedServiceEntity;
import tgc.plus.providedservice.entities.VdsTariff;
import tgc.plus.providedservice.exceptions.facade_exceptions.ResourceNotFoundException;
import tgc.plus.providedservice.facades.utils.FacadesUtils;
import tgc.plus.providedservice.repositories.TariffRepository;
import tgc.plus.providedservice.repositories.custom_database_repository.CustomDatabaseRepository;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
@Slf4j
public class ProvidedFacade {

    @Autowired
    private CustomDatabaseRepository customDatabaseRepository;

    @Autowired
    private FacadesUtils facadesUtils;

    @Autowired
    private TariffRepository tariffRepository;

    @Value("${price.secret}")
    private String priceSecret;

    private final DecimalFormat format = new DecimalFormat("0.00");

    @Transactional(readOnly = true)
    public Mono<TariffData> getTariffById(Integer tariffId){
        return customDatabaseRepository.getTariffDataById(tariffId)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Element with id - %s not found", tariffId)))
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
    public Mono<List<TariffData>> getTariffsInfo(Integer hypervisorId){
        return customDatabaseRepository.getTariffData(hypervisorId)
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public <T extends ProvidedServiceEntity, R> Mono<List<R>> getElemInfo(Class<T> classType, Function<T,R> converter){
        return tariffRepository.getInfoAboutActiveElem(classType)
                .map(converter)
                .collectList()
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServerError();
                });
    }

    @Transactional(readOnly = true)
    public <T extends ProvidedServiceEntity, R> Mono<R> getElemById(Integer elemId, Class<T> classType, Function<T, R> converter){
        return tariffRepository.getInfoAboutElemById(classType, elemId)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Element with id - %s not found", elemId)))
                .map(converter)
                .onErrorResume(e -> {
                    if (!(e instanceof ResourceNotFoundException)){
                        log.error(e.getMessage());
                        return facadesUtils.getServerError();
                    }
                    else
                        return Mono.error(e);
        });
    }

    public <T extends ProvidedServiceEntity> Mono<T> getElemByIdForInside(Integer elemId, Class<T> classType){
        return tariffRepository.getInfoAboutElemById(classType, elemId)
                .filter(Objects::nonNull)
                .switchIfEmpty(facadesUtils.getResourceNotFoundException(String.format("Element with id - %s not found", elemId)))
                .filter(elem -> elem instanceof OperatingSystem || elem.getActive())
                .switchIfEmpty(facadesUtils.getResourceNotFoundException("Tariff parameter no longer supported"));
    }

    //проверка в payment service
    @Transactional
    public Mono<FinalPriceResponse> calculateFinalPrice(Integer tariffId, Integer periodId, Integer ocId){
        return Mono.zip(getElemByIdForInside(tariffId, VdsTariff.class),
                getElemByIdForInside(periodId, Period.class),
                getElemByIdForInside(ocId, OperatingSystem.class))
                .flatMap(result -> {
                    double fullPrice = Math.abs((result.getT1().getPriceMonthKop()* (1-result.getT2().getDiscountPercent()/100.0)) * result.getT2().getCountMonth()+result.getT3().getPriceKop());
                    String monthPrice =  format.format(fullPrice / result.getT2().getCountMonth());
                    log.info(monthPrice);
                    String hashPriceKop = Hashing.hmacSha256(priceSecret.getBytes()).hashBytes(String.valueOf(fullPrice).getBytes()).toString();
                    String hashPriceMonthKop = Hashing.hmacSha256(priceSecret.getBytes()).hashBytes(monthPrice.getBytes()).toString();
                    return Mono.just(new FinalPriceResponse(format.format(fullPrice), hashPriceKop, hashPriceMonthKop));
                })
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
