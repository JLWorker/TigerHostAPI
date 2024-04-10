package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractCharacteristicType implements ProvidedServiceEntity{

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public AbstractCharacteristicType(String typeName) {
        this.typeName = typeName;
    }

    public AbstractCharacteristicType(Integer id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public AbstractCharacteristicType (CharacteristicTypeDto characteristicTypeDto){
        this.id = characteristicTypeDto.getId();
        this.typeName = characteristicTypeDto.getTypeName();
    }

}
