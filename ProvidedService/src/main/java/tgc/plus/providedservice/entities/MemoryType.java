package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;

@Table("memory_types")
@ToString
@NoArgsConstructor
public class MemoryType extends AbstractCharacteristicType {

    public MemoryType(CharacteristicTypeDto characteristicTypeDto) {
        super(characteristicTypeDto);
    }

    public MemoryType(String typeName) {
        super(typeName);
    }

    @Override
    public Boolean getActive() {
        return null;
    }

    @Override
    public String getUniqueElement() {
        return this.getTypeName();
    }
}
