package tgc.plus.providedservice.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewCharacteristicType;

@Table("ram_types")
@NoArgsConstructor
@Setter
@ToString
public class RamAbstractType extends AbstractTypeEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public RamAbstractType(String typeName) {
        this.typeName = typeName;
    }

    public RamAbstractType(NewCharacteristicType characteristicType){
        this.typeName = characteristicType.getTypeName();
    }

}
