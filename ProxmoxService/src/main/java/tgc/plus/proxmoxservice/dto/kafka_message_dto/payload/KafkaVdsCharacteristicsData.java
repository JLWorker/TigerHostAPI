package tgc.plus.proxmoxservice.dto.kafka_message_dto.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KafkaVdsCharacteristicsData {

    @JsonProperty
    private Integer cpu;

    @JsonProperty
    private Integer ram;

    @JsonProperty
    private Integer memory;

    public KafkaVdsCharacteristicsData(Integer cpu, Integer ram, Integer memory) {
        this.cpu = cpu;
        this.ram = ram;
        this.memory = memory;
    }
}
