package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;

@Table("memory_types")
@NoArgsConstructor
@Getter
@ToString
public class MemoryType extends AbstractCharacteristicType {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public MemoryType(String typeName) {
        this.typeName = typeName;
    }

    public MemoryType(CharacteristicTypeDto characteristicType){
        this.typeName = characteristicType.getTypeName();
    }

}
