package tgc.plus.authservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseException {

    public interface VersionInvalid{}

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

    @JsonProperty
    private Long version;

    public ResponseException(String path, Integer status, String error, String message) {
        this.instant = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        this.path = path;
        this.status = status;
        this.error = error;
        this.message = message;
    }

    public ResponseException(String path, Integer status, String error, String message, Long version) {
        this.instant = DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
        this.path = path;
        this.error = error;
        this.status = status;
        this.message = message;
        this.version = version;
    }
}
