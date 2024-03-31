package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("operating_systems")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OperatingSystem {

    @Id
    @Column("id")
    private Integer id;

    @Column("oc_name")
    private String osName;

    @Column("version")
    private String version;

    @Column("bit_depth")
    private Integer bitDepth;

    @Column("template_id")
    private Integer templateId;

    @Column("price")
    private Integer priceKop;

    public OperatingSystem(String osName, String version, Integer bitDepth, Integer templateId, Integer priceKop) {
        this.osName = osName;
        this.version = version;
        this.bitDepth = bitDepth;
        this.templateId = templateId;
        this.priceKop = priceKop;
    }
}
