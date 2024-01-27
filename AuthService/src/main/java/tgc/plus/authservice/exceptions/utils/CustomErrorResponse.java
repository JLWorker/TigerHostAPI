package tgc.plus.authservice.exceptions.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@NoArgsConstructor
public class CustomErrorResponse {

    @JsonProperty
    Long timestamp;

    @JsonProperty
    String path;

    @JsonProperty
    Integer status;

    @JsonProperty
    String error;

    @JsonProperty
    String message;

    public CustomErrorResponse(Long timestamp, String path, Integer status, String error, String message) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }
}
