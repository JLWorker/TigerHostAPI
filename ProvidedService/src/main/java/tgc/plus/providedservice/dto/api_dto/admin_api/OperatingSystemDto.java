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

    public interface Create {}

    public interface Change {}

    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("oc_name")
    @JsonView({Create.class, Change.class})
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\s+]{3,100}$", message = "Invalid operating system name")
    @Schema(example = "Linux Debian")
    private String name;

    @JsonProperty("version")
    @JsonView({Create.class, Change.class})
    @Schema(example = "12")
    @Pattern(regexp = "^[a-zA-Z\\s+\\d+\\.\\-]{1,10}$", message = "Invalid operating system version")
    private String version;

    @JsonProperty("bit_depth")
    @JsonView({Create.class, Change.class})
    @Schema(example = "64")
    @NotNull
    private Integer bitDepth;

    @JsonProperty("template_id")
    @JsonView({Create.class})
    @Schema(example = "121")
    @NotNull
    private Integer templateId;

    @JsonProperty("price_kop")
    @JsonView({Create.class, Change.class})
    @Schema(example = "0")
    @NotNull
    private Integer priceKop;

    @JsonProperty("active")
    @JsonView({Create.class, Change.class})
    @Schema(example = "true")
    @NotNull
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
