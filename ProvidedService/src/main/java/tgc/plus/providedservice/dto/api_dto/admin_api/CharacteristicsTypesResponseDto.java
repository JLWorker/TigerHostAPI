package tgc.plus.providedservice.dto.api_dto.admin_api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CharacteristicsTypesResponseDto {

    @JsonProperty("cpu_types")
    private List<CharacteristicTypeDto> cpuCharacteristicTypes;

    @JsonProperty("ram_types")
    private List<CharacteristicTypeDto> ramCharacteristicTypes;

    @JsonProperty("memory_types")
    private List<CharacteristicTypeDto> memoryCharacteristicTypes;

    public CharacteristicsTypesResponseDto(List<CharacteristicTypeDto> cpuCharacteristicTypes, List<CharacteristicTypeDto> ramCharacteristicTypes, List<CharacteristicTypeDto> memoryCharacteristicTypes) {
        this.cpuCharacteristicTypes = cpuCharacteristicTypes;
        this.ramCharacteristicTypes = ramCharacteristicTypes;
        this.memoryCharacteristicTypes = memoryCharacteristicTypes;
    }
}
