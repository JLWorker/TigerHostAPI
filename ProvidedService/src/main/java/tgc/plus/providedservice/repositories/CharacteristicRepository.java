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
import tgc.plus.providedservice.entities.AbstractCharacteristicType;


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

    public <T extends AbstractCharacteristicType> Mono<Void> saveCharacteristicType(T data, Class<T> typeClass){
        return template.insert(typeClass)
                .using(data)
                .then();
    }

    public <T extends AbstractCharacteristicType> Flux<T> getCharacteristicTypes(Class<T> typeClass){
        return template.select(typeClass).all();
    }

}
