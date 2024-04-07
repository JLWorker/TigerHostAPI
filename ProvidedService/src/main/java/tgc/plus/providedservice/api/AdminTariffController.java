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
import tgc.plus.providedservice.dto.api_dto.admin_api.*;
import tgc.plus.providedservice.entities.*;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin/tariffs")
@Tag(name = "/api/provided/admin/tariffs", description = "Admin controller for setting tariffs")
public class AdminTariffController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @Operation(summary = "Create new tariff")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Tariff with name already exist or has problems with dependencies or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping()
    public Mono<Void> createTariff(@JsonView(TariffDto.Create.class) @Valid @RequestBody TariffDto tariffDto) {
        return adminProvidedFacade.createElement(new VdsTariff(tariffDto), VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
    }

    @Operation(summary = "Change tariff")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Tariff with name already exist or validation exception"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Tariff with id not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/{tariff_id}")
    public Mono<Void> changeTariff(@Valid @RequestBody @JsonView(TariffDto.Change.class) TariffDto tariffDto, @PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.changeTariff(tariffId, tariffDto);
    }

    @Operation(summary = "Delete tariff", description = "You can delete a tariff only if it is not used in user virtual machines")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Tariff not exist or activity status must be turned off"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/{tariff_id}")
    public Mono<Void> deleteTariff(@PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.deleteElement(tariffId, VdsTariff.class);
    }

    @Operation(summary = "Сhange in tariff visibility")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Tariff not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/vision/{tariff_id}")
    public Mono<Void> changeTariffVision(@PathVariable(value = "tariff_id") Integer tariffId) {
        return adminProvidedFacade.changeVision(tariffId, VdsTariff.class, EventTypesList.UPDATE_VDS_TARIFFS);
    }

    @Operation(summary = "Get all tariffs with any visibility status")
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
    public Mono<List<TariffDto>> getAllTariffs(){
        return adminProvidedFacade.getAllRowsElement(VdsTariff.class, TariffDto::new);
    }

}

