package tgc.plus.feedbackgateaway.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import tgc.plus.feedbackgateaway.dto.EventKafkaMessageDto;
import tgc.plus.feedbackgateaway.facade.FeedbackFacade;

@RestController
@RequestMapping("/api/feedback")
@Slf4j
@Tag(name = "api/feedback", description = "Feedback controller api")
public class FeedbackController {

    @Autowired
    FeedbackFacade feedbackFacade;

    @Operation(summary = "Connect to feedback", description = "Get notifications in real time")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "200", content = @Content(schemaProperties = {
                    @SchemaProperty(name = "id", schema = @Schema(example = "2024-03-28T09:30:00Z")),
                    @SchemaProperty(name = "event", schema = @Schema(example = "simple")),
                    @SchemaProperty(name = "body", schema = @Schema(example = "update_account_info"))
            }) ,description = "Successful verify")
    })
    @GetMapping(value = "/events")
    public Flux<ServerSentEvent<EventKafkaMessageDto>> getEvents(){

        return feedbackFacade.getEventsForDevice();

    }

}
