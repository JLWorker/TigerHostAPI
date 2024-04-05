package tgc.plus.providedservice.dto.exceptions_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VersionExceptionResponse extends ExceptionResponse {

    @JsonProperty
    @Schema(example = "5")
    private Long version;

    public VersionExceptionResponse(String path, String error, Integer status, String message, Long version) {
        super(path, error, status, message);
        this.version = version;
    }
}
