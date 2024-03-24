package tgc.plus.proxmoxservice.api;

import jakarta.validation.Valid;
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
public class VmController {

    @Autowired
    private VmFacade vmFacade;

    @GetMapping("/all")
    public Mono<UserAllVms> getAllUserVms(){
        return vmFacade.getAllUserVms();
    }

    @GetMapping("/{vmId}/time")
    public Mono<TimestampsVm> getVmTimestamps(@PathVariable("vmId") String vmId){
        return vmFacade.getVmTimestamps(vmId);
    }

    @GetMapping("/{vmId}/check_trial")
    public Mono<Void> checkVmTrialPeriod(@PathVariable("vmId") String vmId){
        return vmFacade.checkVmTrialPeriod(vmId);
    }

    @GetMapping("/{vmId}/check_storage")
    public Mono<Void> checkVmStorage(@PathVariable("vmId") String vmId, @RequestParam("newSize") Integer newStorageSize){
        return vmFacade.checkVmStorage(vmId, newStorageSize);
    }

    @PostMapping("/user/password")
    public Mono<Void> changeUserPassword(@Valid @RequestBody UserChangePassword userChangePassword){
        return vmFacade.changeUserPassword(userChangePassword);
    }

    @GetMapping("/{vmId}/users")
    public Mono<ProxmoxVmUsersInfo> getActiveUsers(@PathVariable("vmId") String vmId){
        return vmFacade.getVmUsers(vmId);
    }

}
