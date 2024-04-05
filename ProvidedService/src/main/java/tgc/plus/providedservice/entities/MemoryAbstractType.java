package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewCharacteristicType;

@Table("memory_types")
@NoArgsConstructor
@Getter
@ToString
public class MemoryAbstractType extends AbstractTypeEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public MemoryAbstractType(String typeName) {
        this.typeName = typeName;
    }

    public MemoryAbstractType(NewCharacteristicType characteristicType){
        this.typeName = characteristicType.getTypeName();
    }

}
