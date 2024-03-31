package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("memory_types")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MemoryType {

    @Id
    @Column("id")
    private Integer id;

    @Column("type")
    private String typeName;

    public MemoryType(String typeName) {
        this.typeName = typeName;
    }
}
