package tgc.plus.authservice.dto.kafka_message_dto.headers;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MailHeadersDTO implements KafkaHeadersDTO {

    private String command;

    public MailHeadersDTO(String command) {
        this.command = command;
    }
}
