package tgc.plus.authservice.dto.kafka_message_dto.headers;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FeedbackHeadersDTO implements KafkaHeadersDTO {

    private String userCode;

    public FeedbackHeadersDTO(String userCode) {
        this.userCode = userCode;
    }
}
