package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tgc.plus.providedservice.entities.AbstractTypeEntity;


@NoArgsConstructor
@Getter
public class CharacteristicType {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("type")
    private String typeName;

    public CharacteristicType(AbstractTypeEntity characteristicType) {
        this.id = characteristicType.getId();
        this.typeName = characteristicType.getType();
    }
}
