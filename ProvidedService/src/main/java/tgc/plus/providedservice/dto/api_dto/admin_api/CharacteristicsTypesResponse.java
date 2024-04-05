package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CharacteristicsTypesResponse {

    @JsonProperty("cpu_types")
    private List<CharacteristicType> cpuCharacteristicTypes;

    @JsonProperty("ram_types")
    private List<CharacteristicType> ramCharacteristicTypes;

    @JsonProperty("memory_types")
    private List<CharacteristicType> memoryCharacteristicTypes;

    public CharacteristicsTypesResponse(List<CharacteristicType> cpuCharacteristicTypes, List<CharacteristicType> ramCharacteristicTypes, List<CharacteristicType> memoryCharacteristicTypes) {
        this.cpuCharacteristicTypes = cpuCharacteristicTypes;
        this.ramCharacteristicTypes = ramCharacteristicTypes;
        this.memoryCharacteristicTypes = memoryCharacteristicTypes;
    }
}
