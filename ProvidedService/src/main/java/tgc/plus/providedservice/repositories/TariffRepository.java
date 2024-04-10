package tgc.plus.providedservice.repositories;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.configs.R2Config;
import tgc.plus.providedservice.entities.ProvidedServiceEntity;
import tgc.plus.providedservice.entities.VdsTariff;

import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
public class TariffRepository {

    @Autowired
    private R2Config r2Config;

    private R2dbcEntityTemplate template = null;

    @PostConstruct
    private void init(){
        template = r2Config.r2dbcEntityTemplate();
    }

    public <T extends ProvidedServiceEntity> Mono<Boolean> saveElem(T data, Class<T> classType){
        return template.insert(classType)
                .using(data)
                .flatMap(res->Mono.just(res.getActive()));

    }

    public <T extends ProvidedServiceEntity> Mono<Void> updateElem(T data){
        return template.update(data)
                .then();

    }

    public <T extends ProvidedServiceEntity> Mono<Void> changeActive(Integer elemId, Boolean status, Class<T> targetClass){
        return template.update(targetClass)
                .matching(Query.query(where("id").is(elemId)))
                .apply(Update.update("active", !status))
                .then();
    }

    public <T> Mono<Void> deleteElem(Integer elemId, Class<T> targetClass){
        return template.delete(targetClass)
                .matching(Query.query(where("id").is(elemId)))
                .all().then();
    }

    public <T> Flux<T> getInfoAboutActiveElem(Class<T> targetClass){
        return template.select(Query.query(where("active").is(true)), targetClass);
    }

    public <T> Flux<T> getInfoAboutAllRowsElem(Class<T> targetClass){
        return template.select(targetClass)
                .all();
    }

    public Mono<Long> getTariffsByHypervisor(Integer hypervisorId){
        return template.select(Query.query(where("hypervisor_id").is(hypervisorId)), VdsTariff.class)
                .count();
    }

    public <T> Mono<T> getInfoAboutElemById(Class<T> targetClass, Integer elemId){
        return template.selectOne(Query.query(where("id").is(elemId)), targetClass);
    }


}
