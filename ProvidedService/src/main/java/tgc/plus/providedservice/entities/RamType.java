package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("ram_types")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RamType {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public RamType(String typeName) {
        this.typeName = typeName;
    }
}
