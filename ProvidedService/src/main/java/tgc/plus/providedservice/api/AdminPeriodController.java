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
import tgc.plus.providedservice.dto.api_dto.admin_api.ChangePeriodDto;
import tgc.plus.providedservice.dto.api_dto.admin_api.PeriodsDto;
import tgc.plus.providedservice.entities.Period;
import tgc.plus.providedservice.facades.AdminProvidedFacade;
import tgc.plus.providedservice.facades.utils.EventTypesList;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin/periods")
@Tag(name = "/api/provided/admin/periods", description = "Admin controller for setting periods")
public class AdminPeriodController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @Operation(summary = "Create new period")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Period with month count already exist or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping()
    public Mono<Void> createPeriod(@Valid @RequestBody PeriodsDto periodsDto){
        return adminProvidedFacade.createElement(new Period(periodsDto), Period.class, EventTypesList.UPDATE_PERIODS);
    }

    @Operation(summary = "Сhange in period visibility")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Period not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/vision/{period_id}")
    public Mono<Void> changePeriodVision(@PathVariable(value = "period_id") Integer periodId) {
        return adminProvidedFacade.changeVision(periodId, Period.class, EventTypesList.UPDATE_PERIODS);
    }

    @Operation(summary = "Delete period", description = "You can delete a period only if it is not used in user virtual machines")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Period not exist or activity status must be turned off"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/{period_id}")
    public Mono<Void> deletePeriod(@PathVariable(value = "period_id") Integer periodId) {
        return adminProvidedFacade.deleteElement(periodId, Period.class);
    }

    //период отображать только при покупке
    @Operation(summary = "Change period")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Period with count month already exist or validation exception"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Period with id not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/{period_id}")
    public Mono<Void> changePeriod(@PathVariable(value = "period_id") Integer periodId, @RequestBody @Valid ChangePeriodDto periodsDto){
        return adminProvidedFacade.changePeriod(periodId, periodsDto);
    }


    @Operation(summary = "Get all periods with any visibility status")
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
    public Mono<List<PeriodsDto>> getAllPeriods(){
        return adminProvidedFacade.getAllRowsElement(Period.class, PeriodsDto::new);
    }


}
