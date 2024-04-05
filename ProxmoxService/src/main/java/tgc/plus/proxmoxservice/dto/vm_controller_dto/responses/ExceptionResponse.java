package tgc.plus.proxmoxservice.dto.vm_controller_dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponse {

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

    public ExceptionResponse(String path, String error, Integer status, String message) {
        this.instant = String.valueOf(LocalDateTime.now());
        this.path = path;
        this.error = error;
        this.status = status;
        this.message = message;
    }

    public ExceptionResponse(String message) {
        this.message = message;
    }
}
