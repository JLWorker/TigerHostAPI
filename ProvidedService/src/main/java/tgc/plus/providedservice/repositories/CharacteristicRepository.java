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
import tgc.plus.providedservice.entities.AbstractTypeEntity;


import static org.springframework.data.relational.core.query.Criteria.where;

@Repository
public class CharacteristicRepository {

    @Autowired
    private R2Config r2Config;

    private R2dbcEntityTemplate template = null;

    @PostConstruct
    private void init(){
        template = r2Config.r2dbcEntityTemplate();
    }

    public <T extends AbstractTypeEntity> Mono<Void> saveCharacteristicType(T data, Class<T> typeClass){
        return template.insert(typeClass)
                .using(data)
                .then();

    }

    public <T extends AbstractTypeEntity> Flux<T> getCharacteristicTypes(Class<T> typeClass){
        return template.select(typeClass).all();
    }

    public <T extends AbstractTypeEntity> Mono<Void> updateCharacteristicType(String newTypeName, Integer typeId, Class<T> classType){
        return template.update(classType)
                .matching(Query.query(where("id").is(typeId)))
                .apply(Update.update("type", newTypeName))
                .then();
    }

    public <T extends AbstractTypeEntity> Mono<Void> deleteCharacteristicType(Integer typeId, Class<T> typeClass){
        return template.delete(typeClass)
                .matching(Query.query(where("id").is(typeId)))
                .all().then();
    }

}
