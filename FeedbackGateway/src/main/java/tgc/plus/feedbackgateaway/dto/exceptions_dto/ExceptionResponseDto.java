package tgc.plus.feedbackgateaway.dto.exceptions_dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponseDto {

    @JsonProperty
    @Schema(example = "2024-03-29T14:16:03.301810896")
    private String instant;

    @JsonProperty
    @Schema(example = "/api/...")
    private String path;

    @JsonProperty
    @Schema(example = "Unauthorized/Not Found....")
    private String error;

    @JsonProperty
    @Schema(example = "403")
    private Integer status;

    @JsonProperty
    @Schema(example = "Error message")
    private String message;

    public ExceptionResponseDto(String path, String error, Integer status, String message) {
        this.instant = String.valueOf(LocalDateTime.now());
        this.path = path;
        this.error = error;
        this.status = status;
        this.message = message;
    }

    public ExceptionResponseDto(String message) {
        this.message = message;
    }
}
