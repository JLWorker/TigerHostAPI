package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.OperatingSystem;

@NoArgsConstructor
@Getter
@Setter
public class OcData {

    @JsonProperty("os_id")
    private Integer osId;

    @JsonProperty("os_name")
    private String osName;

    @JsonProperty("version")
    private String version;

    @JsonProperty("bit_depth")
    private Integer bitDepth;

    @JsonProperty("template_id")
    private Integer templateId;

    @JsonProperty("price")
    private Float priceRub;

    public OcData(OperatingSystem system) {
        this.osId = system.getId();
        this.osName = system.getOsName();
        this.version = system.getVersion();
        this.bitDepth = system.getBitDepth();
        this.templateId = system.getTemplateId();
        this.priceRub = (float) system.getPriceKop() % 100;
    }
}
