package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.providedservice.entities.AbstractCharacteristicType;

@Setter
@Getter
@NoArgsConstructor
public class CharacteristicTypeDto {


    @JsonProperty("id")
    @JsonView()
    @Schema(example = "1")
    private Integer id;

    @JsonProperty("type_name")
    @Schema(example = "DDR4/SSD/Ryzen7")
    @Pattern(regexp = "^[A-Za-zА-Яа-я\\s+\\d+\\-]{2,100}$", message = "Invalid type name")
    @NotBlank(message = "Type name parameter mustn't be null or empty")
    private String typeName;

    public CharacteristicTypeDto(String typeName) {
        this.typeName = typeName;
    }

    public CharacteristicTypeDto(AbstractCharacteristicType abstractCharacteristicType){
        this.id = abstractCharacteristicType.getId();
        this.typeName = abstractCharacteristicType.getTypeName();
    }
}
