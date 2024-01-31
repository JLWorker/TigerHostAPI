package tgc.plus.authservice.dto.exceptions_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VersionResponseException extends ResponseException {

    @JsonProperty
    private Long version;

    public VersionResponseException(String path, String error, Integer status, String message, Long version) {
        super(path, error, status, message);
        this.version = version;
    }
}
