package tgc.plus.providedservice.api;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.admin_api.OperatingSystemDto;
import tgc.plus.providedservice.entities.OperatingSystem;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin/ocs")
@Tag(name = "/api/provided/admin/ocs", description = "Admin controller for setting operating systems")
public class AdminOperatingSystemController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @Operation(summary = "Create new operating system")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Operating system with template id already exist or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping()
    public Mono<Void> createOperatingSystem(@RequestBody @Valid OperatingSystemDto operatingSystemDto){
        return adminProvidedFacade.createElement(new OperatingSystem(operatingSystemDto), OperatingSystem.class, EventTypesList.UPDATE_OC);
    }

    @Operation(summary = "Change operating system")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Operating system with template id already exist or validation exception"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Operating system with id not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/{oc_id}")
    public Mono<Void> changeOperatingSystem(@PathVariable(value = "oc_id") Integer ocId, @RequestBody @Valid @JsonView(OperatingSystemDto.Change.class) OperatingSystemDto operatingSystemDto) {
        return adminProvidedFacade.changeOperatingSystem(ocId, operatingSystemDto);
    }

    @Operation(summary = "Delete operating system", description = "You can delete a operating system only if it is not used in user virtual machines")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Operating system not exist or activity status must be turned off"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/{oc_id}")
    public Mono<Void> deleteOperatingSystem(@PathVariable(value = "oc_id") Integer ocId) {
        return adminProvidedFacade.deleteElement(ocId, OperatingSystem.class);
    }

    @Operation(summary = "Get all operating systems with any visibility status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping()
    public Mono<List<OperatingSystemDto>> getAllOperatingSystems(){
        return adminProvidedFacade.getAllRowsElement(OperatingSystem.class, OperatingSystemDto::new);
    }

}
