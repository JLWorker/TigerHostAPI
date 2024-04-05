package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class AbstractTypeEntity {

    private Integer id;

    private String type;

    public AbstractTypeEntity(Integer id, String type) {
        this.id = id;
        this.type = type;
    }
}
