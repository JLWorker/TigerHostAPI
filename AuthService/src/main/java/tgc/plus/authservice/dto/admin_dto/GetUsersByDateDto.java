package tgc.plus.authservice.dto.admin_dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class GetUsersByDateDto {

    @Pattern(regexp = "(^\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]$)", message = "Incorrect start date")
    @NotBlank(message = "Start date mustn't be null or empty")
    @Schema(example = "2024-04-23")
    private String startDate;

    @Pattern(regexp = "(^\\d{4})-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]$)", message = "Incorrect finish date")
    @NotBlank(message = "Finish date mustn't be null or empty")
    @Schema(example = "2024-04-29")
    private String finishDate;

    @Positive(message = "Limit can't be negative")
    @Schema(example = "10")
    private Integer limit;

}
