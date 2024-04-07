package tgc.plus.providedservice.entities;

import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;

@Table("cpu_types")
@Setter
@ToString
public class CpuType extends AbstractCharacteristicType {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public CpuType(String typeName) {
        this.typeName = typeName;
    }

    public CpuType(CharacteristicTypeDto characteristicType){
        this.typeName = characteristicType.getTypeName();
    }

}
