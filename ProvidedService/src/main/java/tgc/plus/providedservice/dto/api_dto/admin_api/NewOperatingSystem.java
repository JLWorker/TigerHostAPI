package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class NewOperatingSystem {

    public interface Create {}

    public interface Change {}

    @JsonProperty("oc_name")
    @JsonView({Create.class, Change.class})
    private String name;

    @JsonProperty("version")
    @JsonView({Create.class, Change.class})
    private String version;

    @JsonProperty("bit_depth")
    @JsonView({Create.class, Change.class})
    private Integer bitDepth;

    @JsonProperty("template_id")
    @JsonView({Create.class})
    private Integer templateId;

    @JsonProperty("price_kop")
    @JsonView({Create.class, Change.class})
    private Integer priceKop;

    @JsonProperty("active")
    @JsonView({Create.class, Change.class})
    private Boolean active;

    public NewOperatingSystem(String name, String version, Integer bitDepth, Integer templateId, Integer priceKop, Boolean active) {
        this.name = name;
        this.version = version;
        this.bitDepth = bitDepth;
        this.templateId = templateId;
        this.priceKop = priceKop;
        this.active = active;
    }

}
