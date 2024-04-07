package tgc.plus.providedservice.dto.api_dto.simple_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.OperatingSystem;

@NoArgsConstructor
@Getter
@Setter
public class OcData {

    @JsonProperty("os_id")
    @Schema(example = "1")
    private Integer osId;

    @JsonProperty("os_name")
    @Schema(example = "Linux Debian")
    private String osName;

    @JsonProperty("version")
    @Schema(example = "12")
    private String version;

    @JsonProperty("bit_depth")
    @Schema(example = "64")
    private Integer bitDepth;

    @JsonProperty("template_id")
    @Schema(example = "121")
    private Integer templateId;

    @JsonProperty("price_kop")
    @Schema(example = "0")
    private Integer priceKop;

    public OcData(OperatingSystem system) {
        this.osId = system.getId();
        this.osName = system.getOsName();
        this.version = system.getVersion();
        this.bitDepth = system.getBitDepth();
        this.templateId = system.getTemplateId();
        this.priceKop = system.getPriceKop();
    }
}
