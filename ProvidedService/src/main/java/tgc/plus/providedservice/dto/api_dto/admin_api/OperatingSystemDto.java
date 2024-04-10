package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.OperatingSystem;

@NoArgsConstructor
@Getter
@Setter
public class OperatingSystemDto {

    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("oc_name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\s+]{3,100}$", message = "Invalid operating system name")
    @Schema(example = "Linux Debian")
    @NotBlank(message = "Oc name parameter mustn't be null or empty")
    private String name;

    @JsonProperty("version")
    @Schema(example = "12")
    @Pattern(regexp = "^[a-zA-Z\\s+\\d+\\.\\-]{1,10}$", message = "Invalid operating system version")
    @NotNull(message = "Version parameter mustn't be null or empty")
    private String version;

    @JsonProperty("bit_depth")
    @Schema(example = "64")
    @NotNull(message = "Bit depth parameter mustn't be null")
    private Integer bitDepth;

    @JsonProperty("template_id")
    @Schema(example = "121")
    @NotNull(message = "Template id parameter mustn't be null")
    private Integer templateId;

    @JsonProperty("price_kop")
    @Schema(example = "0")
    @NotNull(message = "Price parameter mustn't be null")
    private Integer priceKop;

    @JsonProperty("active")
    @Schema(example = "true")
    @NotNull(message = "Active parameter mustn't be null")
    private Boolean active;

    public OperatingSystemDto(OperatingSystem operatingSystem) {
        this.id = operatingSystem.getId();
        this.name = operatingSystem.getOsName();
        this.version = operatingSystem.getVersion();
        this.bitDepth = operatingSystem.getBitDepth();
        this.templateId = operatingSystem.getTemplateId();
        this.priceKop = operatingSystem.getPriceKop();
        this.active = operatingSystem.getActive();
    }

}
