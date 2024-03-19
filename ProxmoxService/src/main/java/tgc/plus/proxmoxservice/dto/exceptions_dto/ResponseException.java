package tgc.plus.proxmoxservice.dto.exceptions_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ResponseException {

    @JsonProperty
    private String instant;

    @JsonProperty
    private String path;

    @JsonProperty
    private String error;

    @JsonProperty
    private Integer status;

    @JsonProperty
    private String message;

    public ResponseException(String path, String error, Integer status, String message) {
        this.instant = String.valueOf(LocalDateTime.now());
        this.path = path;
        this.error = error;
        this.status = status;
        this.message = message;
    }

    public ResponseException(String message) {
        this.message = message;
    }
}
