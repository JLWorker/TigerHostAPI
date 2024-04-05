package tgc.plus.providedservice.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import tgc.plus.providedservice.dto.api_dto.admin_api.NewOperatingSystem;

@Table("operating_systems")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class OperatingSystem implements TariffEntity {

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

    @Column("active")
    private Boolean active;

    public OperatingSystem(String osName, String version, Integer bitDepth, Integer templateId, Integer priceKop) {
        this.osName = osName;
        this.version = version;
        this.bitDepth = bitDepth;
        this.templateId = templateId;
        this.priceKop = priceKop;
    }

    public OperatingSystem(NewOperatingSystem operatingSystem){
        this.osName = operatingSystem.getName();
        this.version = operatingSystem.getVersion();
        this.bitDepth = operatingSystem.getBitDepth();
        this.templateId = operatingSystem.getTemplateId();
        this.priceKop = operatingSystem.getPriceKop();
        this.active = operatingSystem.getActive();
    }

    @Override
    public Boolean getActiveStatus() {
        return getActive();
    }

    @Override
    public String getUniqueElement() {
        return getTemplateId().toString();
    }
}
