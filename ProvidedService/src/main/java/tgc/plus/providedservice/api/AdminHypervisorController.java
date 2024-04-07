package tgc.plus.providedservice.api;

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
import tgc.plus.providedservice.dto.api_dto.admin_api.HypervisorDto;
import tgc.plus.providedservice.entities.Hypervisor;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin/hypers")
@Tag(name = "/api/provided/admin/hypers", description = "Admin controller for setting hypervisors")
public class AdminHypervisorController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @Operation(summary = "Create new hypervisor")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Hypervisor with name already exist or has problems with dependencies or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping()
    public Mono<Void> createHypervisor(@RequestBody @Valid HypervisorDto hypervisorDto){
        return adminProvidedFacade.createElement(new Hypervisor(hypervisorDto), Hypervisor.class, EventTypesList.UPDATE_HYPERVISORS);
    }

    @Operation(summary = "Get all hypervisors with any visibility status")
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
    public Mono<List<HypervisorDto>> getAllHypervisors(){
        return adminProvidedFacade.getAllRowsElement(Hypervisor.class, HypervisorDto::new);
    }

    @Operation(summary = "Сhange in operating system visibility")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Operating system not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/{hyper_id}")
    public Mono<Void> changeOperatingSystem(@PathVariable(value = "hyper_id") Integer hypervisorId, @RequestBody @Valid HypervisorDto hypervisorDto) {
        return adminProvidedFacade.changeHypervisor(hypervisorId, hypervisorDto);
    }

    @Operation(summary = "Delete operating system", description = "You can delete a operating system only if it is not used in tariffs")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Operating system not exist or activity status must be turned off"),
            @ApiResponse(responseCode = "409", content = @Content(), description = "Operating system related elements are present"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/{hyper_id}")
    public Mono<Void> deleteHypervisor(@PathVariable(value = "hyper_id") Integer hypervisorId) {
        return adminProvidedFacade.deleteHypervisor(hypervisorId);
    }


}
