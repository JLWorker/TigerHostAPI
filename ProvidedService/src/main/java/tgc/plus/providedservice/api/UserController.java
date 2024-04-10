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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.providedservice.dto.api_dto.simple_api.*;
import tgc.plus.providedservice.entities.Hypervisor;
import tgc.plus.providedservice.entities.OperatingSystem;
import tgc.plus.providedservice.entities.Period;
import tgc.plus.providedservice.facades.ProvidedFacade;

import java.util.List;

@RestController
@RequestMapping("/api/provided")
@Tag(name = "api/provided", description = "Publish, basic provided controller")
public class UserController {

    @Autowired
    private ProvidedFacade providedFacade;

    //пользователю вместе с передачей в сервис оплаты тарифа нужно передать будет еще и гипервизор

    @Operation(summary = "Get active tariffs by hypervisor identify")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @Parameter(name = "hyper_id", in = ParameterIn.PATH, example = "2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/hyper/{hyper_id}/tariffs")

    public Mono<List<TariffDataDto>> getTariffsInfo(@PathVariable("hyper_id") Integer hypervisorId){
        return providedFacade.getActiveTariffsInfo(hypervisorId);
    }

    @Operation(summary = "Get all active periods")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/periods")
    public Mono<List<PeriodDataDto>> getPeriodsInfo(){
        return providedFacade.getActiveElemInfo(Period.class, PeriodDataDto::new);
    }

    @Operation(summary = "Get all active operating systems")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/ocs")
    public Mono<List<OcDataDto>> getOperatingSystemsInfo(){
        return providedFacade.getActiveElemInfo(OperatingSystem.class, OcDataDto::new);
    }


    @Operation(summary = "Get tariff by identify", description = "Receives a tariff regardless of activity status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @Parameter(name = "id", in = ParameterIn.PATH, example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Tariff with id not found"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/tariffs/{id}")
    public Mono<TariffDataDto> getTariffById(@PathVariable(value = "id") Integer id){
        return providedFacade.getTariffById(id);
    }

    @Operation(summary = "Get period by identify", description = "Receives a period regardless of activity status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @Parameter(name = "id", in = ParameterIn.PATH, example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Period with id not found"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/periods/{id}")
    public Mono<PeriodDataDto> getPeriodById(@PathVariable(value = "id") Integer id){
        return providedFacade.getElemById(id, Period.class, PeriodDataDto::new);
    }


    @Operation(summary = "Get operating system by identify", description = "Receives a operating system regardless of activity status")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @Parameter(name = "id", in = ParameterIn.PATH, example = "1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Operating system with id not found"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/ocs/{id}")
    public Mono<OcDataDto> getOperatingSystemById(@PathVariable(value = "id") Integer id){
        return providedFacade.getElemById(id, OperatingSystem.class, OcDataDto::new);
    }

    @Operation(summary = "Get all active hypervisors")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/hyper")
    public Mono<List<HypervisorDataDto>> getHypervisors(){
        return providedFacade.getActiveElemInfo(Hypervisor.class, HypervisorDataDto::new);
    }

    @Operation(summary = "Calculate final cost for tariff")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Tariff parameter not found or parameter no longer supported (tariff, oc, period) "),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/final_cost")
    public Mono<FinalPriceResponseDto> getFinalCostByTariff(@RequestParam("tariff_id") Integer tariffId, @RequestParam("period_id") Integer periodId,
                                                            @RequestParam("oc_id") Integer ocId){
        return providedFacade.calculateFinalPrice(tariffId, periodId, ocId);
    }

}
