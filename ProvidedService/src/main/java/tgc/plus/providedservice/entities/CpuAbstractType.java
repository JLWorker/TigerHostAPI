package tgc.plus.providedservice.entities;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewCharacteristicType;

@Table("cpu_types")
@NoArgsConstructor
@Setter
@ToString
public class CpuAbstractType extends AbstractTypeEntity {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public CpuAbstractType(String typeName) {
        this.typeName = typeName;
    }

    public CpuAbstractType(NewCharacteristicType characteristicType){
        this.typeName = characteristicType.getTypeName();
    }

}
