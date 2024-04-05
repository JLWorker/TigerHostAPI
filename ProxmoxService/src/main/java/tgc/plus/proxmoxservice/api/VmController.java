package tgc.plus.proxmoxservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmUsersInfo;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.UserAllVms;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.requests.UserChangePassword;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.TimestampsVm;
import tgc.plus.proxmoxservice.facades.VmFacade;

@RequestMapping("/api/vm")
@RestController
@Tag(name = "api/vm", description = "Virtual machine controller api")
public class VmController {

    @Autowired
    private VmFacade vmFacade;

    @Operation(summary = "Get all user virtual machines")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/all")
    public Mono<UserAllVms> getAllUserVms(){
        return vmFacade.getAllUserVms();
    }

    @Operation(summary = "Get timestamps vm", description = "Return date of create and expired date")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @Parameter(name = "vmId", in = ParameterIn.PATH, description = "Virtual machine identity", example = "vm_84787327")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Virtual machine not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/{vmId}/time")
    public Mono<TimestampsVm> getVmTimestamps(@Pattern(regexp = "^VM-\\d+$") @PathVariable("vmId") String vmId){
        return vmFacade.getVmTimestamps(vmId);
    }

    @Operation(summary = "Check vm trial period")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @Parameter(name = "vmId", in = ParameterIn.PATH, description = "Virtual machine identity", example = "vm_84787327")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "403", content = @Content(), description = "Virtual machine trial period expired"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Virtual machine not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/{vmId}/check_trial")
    public Mono<Void> checkVmTrialPeriod(@Pattern(regexp = "^VM-\\d+$") @PathVariable("vmId") String vmId){
        return vmFacade.checkVmTrialPeriod(vmId);
    }

    @Operation(summary = "Check vm storage")
    @Parameters(value = {
            @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd..."),
            @Parameter(name = "vmId", in = ParameterIn.PATH, description = "Virtual machine identity", example = "vm_84787327"),
            @Parameter(name = "newSize", in = ParameterIn.QUERY, description = "New virtual machine size", example = "25")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Virtual machine storage is smaller than new"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Virtual machine not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @GetMapping("/{vmId}/check_storage")
    public Mono<Void> checkVmStorage(@Pattern(regexp = "^VM-\\d+$") @PathVariable("vmId") String vmId, @RequestParam("newSize") Integer newStorageSize){
        return vmFacade.checkVmStorage(vmId, newStorageSize);
    }

    @Operation(summary = "Change user vm password")
    @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd...")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "400", content = @Content(), description = "Passwords mismatch or invalid validation"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Virtual machine not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })
    @PostMapping("/user/password")
    public Mono<Void> changeUserPassword(@Valid @RequestBody UserChangePassword userChangePassword){
        return vmFacade.changeUserPassword(userChangePassword);
    }

    @Operation(summary = "Get active user in vm")
    @Parameters(value = {
        @Parameter(name = "Authorization", in = ParameterIn.HEADER, description = "Access token", example = "Bearer_uhdYUhskn879jd..."),
        @Parameter(name = "vmId", in = ParameterIn.PATH, example = "vm_8787487")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", content = @Content(), headers = {
                    @Header(name = "Expired", description = "Access token expired, need update", schema = @Schema(example = "true")),
                    @Header(name = "Logout", description = "User from access token not exist, need logout", schema = @Schema(example = "true"))
            }, description = "Invalid access token"),
            @ApiResponse(responseCode = "204", content = @Content(), description = "No active user in vm"),
            @ApiResponse(responseCode = "404", content = @Content(), description = "Virtual machine not exist"),
            @ApiResponse(responseCode = "500", content = @Content(), description = "Error in work inside service"),
            @ApiResponse(responseCode = "200", description = "Success operation")
    })

    @GetMapping("/{vmId}/users")
    public Mono<ProxmoxVmUsersInfo> getActiveUsers(@Pattern(regexp = "^VM-\\d+$") @PathVariable("vmId") String vmId){
        return vmFacade.getVmUsers(vmId);
    }

}
