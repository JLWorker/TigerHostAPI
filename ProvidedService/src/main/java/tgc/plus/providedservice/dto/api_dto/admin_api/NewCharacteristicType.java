package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class NewCharacteristicType {


    @JsonProperty("type_name")
    @Pattern(regexp = "^[A-Za-zА-Яа-я\\s+\\d+\\-]{3,100}$", message = "Invalid type name")
    private String typeName;

    public NewCharacteristicType(String typeName) {
        this.typeName = typeName;
    }
}
