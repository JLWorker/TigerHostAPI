package tgc.plus.authservice.dto.kafka_message_dto.headers;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FeedbackHeadersDto implements KafkaHeadersDto {

    private String userCode;

    public FeedbackHeadersDto(String userCode) {
        this.userCode = userCode;
    }
}
