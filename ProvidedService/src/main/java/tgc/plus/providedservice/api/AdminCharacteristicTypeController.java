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
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicTypeDto;
import tgc.plus.providedservice.dto.api_dto.admin_api.CharacteristicsTypesResponse;
import tgc.plus.providedservice.entities.CpuType;
import tgc.plus.providedservice.entities.MemoryType;
import tgc.plus.providedservice.entities.RamType;
import tgc.plus.providedservice.facades.AdminProvidedFacade;

import java.util.List;

@RestController
@RequestMapping("/api/provided/admin/types")
@Tag(name = "/api/provided/admin/types", description = "Admin controller for setting types of tariff characteristics (ram_type, cpu_type, mem_type)")
public class AdminCharacteristicTypeController {

    @Autowired
    private AdminProvidedFacade adminProvidedFacade;

    @Operation(summary = "Get all characteristic types", description = "Return cpu_types, ram_types, mem_types")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/all")
    public Mono<CharacteristicsTypesResponse> getCharacteristicsTypes(){
        return adminProvidedFacade.getCharacteristicsTypes();
    }

    @Operation(summary = "Get cpu types")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/cpu")
    public Mono<List<CharacteristicTypeDto>> getCpuTypes(){
        return adminProvidedFacade.getCharacteristicType(CpuType.class);
    }


    @Operation(summary = "Create cpu type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Cpu type already exist or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping("/cpu")
    public Mono<Void> createCpuType(@RequestBody @JsonView(CharacteristicTypeDto.Create.class) @Valid CharacteristicTypeDto characteristicType){
        return adminProvidedFacade.createCharacteristicType(new CpuType(characteristicType), CpuType.class);
    }

    @Operation(summary = "Сhange cpu type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Cpu type not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/cpu/{type_id}")
    public Mono<Void> changeCpuType(@RequestBody @JsonView(CharacteristicTypeDto.Change.class) @Valid CharacteristicTypeDto characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, CpuType.class);
    }

    @Operation(summary = "Delete cpu type", description = "You can delete a cpu type only if it is not used in user vds")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Cpu type not exist"),
            @ApiResponse(responseCode = "409", content = @Content(), description = "Cpu type related elements are present"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/cpu/{type_id}")
    public Mono<Void> deleteCpuType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, CpuType.class);
    }

    @Operation(summary = "Get ram types")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/ram")
    public Mono<List<CharacteristicTypeDto>> getRamTypes(){
        return adminProvidedFacade.getCharacteristicType(RamType.class);
    }

    @Operation(summary = "Create ram type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Ram type already exist or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping("/ram")
    public Mono<Void> createRamType(@RequestBody @JsonView(CharacteristicTypeDto.Create.class) @Valid CharacteristicTypeDto characteristicType){
        return adminProvidedFacade.createCharacteristicType(new RamType(characteristicType), RamType.class);
    }


    @Operation(summary = "Сhange ram type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Ram type not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/ram/{type_id}")
    public Mono<Void> changeRamType(@RequestBody @Valid @JsonView(CharacteristicTypeDto.Change.class) CharacteristicTypeDto characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, RamType.class);
    }

    @Operation(summary = "Delete ram type", description = "You can delete a ram type only if it is not used in user vds")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Ram type not exist"),
            @ApiResponse(responseCode = "409", content = @Content(), description = "Ram type related elements are present"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/ram/{type_id}")
    public Mono<Void> deleteRamType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, RamType.class);
    }

    @Operation(summary = "Get memory types")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/mem")
    public Mono<List<CharacteristicTypeDto>> getMemoryTypes(){
        return adminProvidedFacade.getCharacteristicType(MemoryType.class);
    }

    @Operation(summary = "Create memory type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Memory type already exist or validation exception"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping("/mem")
    public Mono<Void> createMemoryType(@RequestBody @JsonView(CharacteristicTypeDto.Create.class) @Valid CharacteristicTypeDto characteristicType){
        return adminProvidedFacade.createCharacteristicType(new MemoryType(characteristicType), MemoryType.class);
    }

    @Operation(summary = "Сhange memory type")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Memory type not found"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PatchMapping("/mem/{type_id}")
    public Mono<Void> changeMemoryType(@RequestBody @JsonView(CharacteristicTypeDto.Change.class) @Valid CharacteristicTypeDto characteristicType, @PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.changeCharacteristicType(characteristicType.getTypeName(), typeId, MemoryType.class);
    }

    @Operation(summary = "Delete memory type", description = "You can delete a memory type only if it is not used in user vds")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, example = "Bearer_uthd8674Jdbai9....", description = "ONLY FOR USER WITH ADMIN ROLE!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Invalid params in request"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Memory type not exist"),
            @ApiResponse(responseCode = "409", content = @Content(), description = "Memory type related elements are present"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Internal errors in request processing"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @DeleteMapping("/mem/{type_id}")
    public Mono<Void> deleteMemoryType(@PathVariable(value = "type_id") Integer typeId){
        return adminProvidedFacade.deleteCharacteristicType(typeId, MemoryType.class);
    }


}
