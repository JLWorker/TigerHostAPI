package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractCharacteristicType{

    private Integer id;
    private String type;

    public AbstractCharacteristicType(Integer id, String type) {
        this.id = id;
        this.type = type;
    }
}
