package tgc.plus.proxmoxservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests.VmUserSetPassword;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.VmDiskInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.VmPartitionDiskData;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.UserAllVms;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.requests.UserChangePassword;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.TimestampsVm;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.PasswordsMismatchException;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.TrialPeriodExpired;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.VmNotFoundException;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.VmStorageEnoughException;
import tgc.plus.proxmoxservice.facades.utils.FacadesUtils;
import tgc.plus.proxmoxservice.services.utils.ProxmoxUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import static tgc.plus.proxmoxservice.services.utils.paths.VmClusterPaths.CHANGE_VM_USER_PASSWORD;
import static tgc.plus.proxmoxservice.services.utils.paths.VmClusterPaths.GET_VM_DISKS_INFO;

@Component
@Slf4j
public class VmFacade {

    @Autowired
    private FacadesUtils facadesUtils;

    @Value("${trial.period}")
    private Integer trialPeriod;

    @Value("${memory.safe-amount}")
    private Integer memorySafeAmount;

    @Autowired
    private ProxmoxUtils proxmoxUtils;

    @Transactional
    public Mono<UserAllVms> getAllUserVms() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String userCode = (String) securityContext.getAuthentication().getPrincipal();
                    return facadesUtils.getAllUserVms(userCode);
                })
                .onErrorResume(e -> {
                    log.error(e.getMessage());
                    return facadesUtils.getServiceException("The service cannot process the request");
                });

    }

    @Transactional
    public Mono<TimestampsVm> getVmTimestamps(String vmId) { //для изменения тарифа, реализовать проверку при изменении еще
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String userCode = (String) securityContext.getAuthentication().getPrincipal();
                    return facadesUtils.getVdsByUserCodeAndVmId(userCode, vmId)
                            .flatMap(vds -> Mono.just(new TimestampsVm(vds.getStartDate(), vds.getExpiredDate())));
                })
                .doOnError(e -> log.error(e.getMessage()));
    }

    @Transactional
    public Mono<Void> checkVmTrialPeriod(String vmId) {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> {
                    String userCode = (String) securityContext.getAuthentication().getPrincipal();
                    return facadesUtils.getVdsByUserCodeAndVmId(userCode, vmId)
                            .filter(vds -> vds.getStartDate().plus(Duration.ofDays(trialPeriod)).isAfter(Instant.now()))
                            .switchIfEmpty(Mono.error(new TrialPeriodExpired("Free period expired")))
                            .then();
                })
                .doOnError(e -> {
                    if (!(e instanceof TrialPeriodExpired)){
                        log.error(e.getMessage());
                    }
                } );
    }

    @Transactional
    public Mono<Void> checkVmStorage(String vmId, Integer newStorageSize) {
        return facadesUtils.getUserCodeFromContext().flatMap(userCode ->
                        facadesUtils.getVdsByUserCodeAndVmId(userCode, vmId)
                                .flatMap(vds -> proxmoxUtils.sendGetRequestToCluster(Map.of("vmId", vmId, "node", vds.getVmNode()), GET_VM_DISKS_INFO, VmDiskInfo.class))
                                .flatMap(disksData -> Flux.fromIterable(disksData.getVmDisks()).map(VmPartitionDiskData::getUsedPartitionSize)
                                        .reduce(0L, Long::sum))
                                .filter(usedSize -> usedSize > 0 && usedSize + memorySafeAmount < newStorageSize)
                                .switchIfEmpty(Mono.error(new VmStorageEnoughException("The new storage size is not enough")))
                                .then())
                .onErrorResume(e -> {
                    if (!(e instanceof VmStorageEnoughException) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("Vm not available at the moment");
                    } else
                        return Mono.empty();
                });
    }

    @Transactional
    public Mono<Void> changeUserPassword(UserChangePassword userChangePassword){
        return facadesUtils.getUserCodeFromContext().flatMap(userCode ->{
                    if (!userChangePassword.getPassword().equals(userChangePassword.getPasswordConfirm()))
                        return Mono.error(new PasswordsMismatchException("Passwords mismatch"));
                    else
                        return facadesUtils.getVdsByUserCodeAndVmId(userCode, userChangePassword.getVmId())
                                .flatMap(vds -> proxmoxUtils.sendChangeRequestToCluster(HttpMethod.POST, Map.of("vmId", userChangePassword.getVmId(), "node", vds.getVmNode()),
                                        new VmUserSetPassword(userChangePassword.getUsername(), userChangePassword.getPassword()), CHANGE_VM_USER_PASSWORD));
                })
                .onErrorResume(e -> {
                    if (!(e instanceof PasswordsMismatchException) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("Vm not available at the moment or user data invalid");
                    } else
                        return Mono.empty();
                });
    }

    //еще информацию о пользователях



}


