package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;

@Table("cpu_types")
@ToString
@NoArgsConstructor
public class CpuType extends AbstractCharacteristicType {

    public CpuType(CharacteristicTypeDto characteristicTypeDto) {
        super(characteristicTypeDto);
    }

    public CpuType(String typeName) {
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
