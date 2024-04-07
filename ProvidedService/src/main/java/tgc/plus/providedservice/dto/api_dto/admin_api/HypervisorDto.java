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
import tgc.plus.providedservice.entities.Hypervisor;

@Setter
@Getter
@NoArgsConstructor
public class HypervisorDto {

    public interface Create {}
    public interface Change {}

    @JsonProperty
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("hypervisor_name")
    @JsonView({Create.class, Change.class})
    @Schema(example = "Proxmox")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\s+]{3,100}$", message = "Invalid hypervisor name")
    private String name;

    @JsonProperty
    @NotNull
    @JsonView({Create.class, Change.class})
    @Schema(example = "true")
    private Boolean active;

    public HypervisorDto(Hypervisor hypervisor) {
        this.id = hypervisor.getId();
        this.name = hypervisor.getName();
        this.active = hypervisor.getActive();
    }
}
