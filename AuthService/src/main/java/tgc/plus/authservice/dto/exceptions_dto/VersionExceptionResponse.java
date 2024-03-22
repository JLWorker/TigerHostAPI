package tgc.plus.authservice.dto.exceptions_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VersionExceptionResponse extends ExceptionResponse {

    @JsonProperty
    private Long version;

    public VersionExceptionResponse(String path, String error, Integer status, String message, Long version) {
        super(path, error, status, message);
        this.version = version;
    }
}
