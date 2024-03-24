package tgc.plus.proxmoxservice.facades;

import com.ongres.scram.common.bouncycastle.pbkdf2.SHA256Digest;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.KSQLWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.configs.SpringSecurityConfig;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests.ProxmoxVmUserSetPassword;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmDiskInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmUsersInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.vm_payloads.ProxmoxVmPartitionDiskData;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.UserAllVms;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.requests.UserChangePassword;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.TimestampsVm;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.*;
import tgc.plus.proxmoxservice.facades.utils.FacadesUtils;
import tgc.plus.proxmoxservice.services.utils.ProxmoxUtils;

import java.security.MessageDigest;
import java.security.MessageDigestSpi;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static tgc.plus.proxmoxservice.services.utils.paths.VmClusterPaths.*;

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

    @Autowired
    private SpringSecurityConfig springSecurityConfig;

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
                .onErrorResume(e -> {
                    if(!(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("The service cannot process the request");
                    }
                    else
                        return Mono.error(e);

                });
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
                .onErrorResume(e -> {
                    if (!(e instanceof TrialPeriodExpired) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("The service cannot process the request");
                    }
                    else
                        return Mono.error(e);
                    });
    }

    @Transactional
    public Mono<Void> checkVmStorage(String vmId, Integer newStorageSize) {
        return facadesUtils.getUserCodeFromContext().flatMap(userCode ->
                        facadesUtils.getVdsByUserCodeAndVmId(userCode, vmId))
                                .flatMap(vds -> proxmoxUtils.sendGetRequestToCluster(Map.of("node", vds.getVmNode(), "vmId", vds.getVmNumber()), GET_VM_DISKS_INFO, ProxmoxVmDiskInfo.class))
                                .flatMap(disksData -> Flux.fromIterable(disksData.getVmDisks()).flatMap(proxmoxVmPartitionDiskData -> Mono.just(proxmoxVmPartitionDiskData.getUsedPartitionSize()))
                                        .reduce(0L, Long::sum))
                                .filter(usedSize -> {
                                    double gb = usedSize/Math.pow(1024, 3);
                                    return gb > 0 && gb + memorySafeAmount < newStorageSize;
                                })
                                .switchIfEmpty(Mono.error(new VmStorageEnoughException("The new storage size is not enough")))
                                .then()
                .onErrorResume(e -> {
                    if (!(e instanceof VmStorageEnoughException) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("Vm not available at the moment");
                    } else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<Void> changeUserPassword(UserChangePassword userChangePassword){
        return facadesUtils.getUserCodeFromContext().flatMap(userCode -> {
            if (!userChangePassword.getPassword().equals(userChangePassword.getPasswordConfirm()))
                return Mono.error(new PasswordsMismatchException("Passwords mismatch"));
            else
                return facadesUtils.getVdsByUserCodeAndVmId(userCode, userChangePassword.getVmId())
                        .flatMap(vds -> proxmoxUtils.sendChangeRequestToCluster(HttpMethod.POST, Map.of("vmId", vds.getVmNumber(), "node", vds.getVmNode()),
                                new ProxmoxVmUserSetPassword(userChangePassword.getUsername(), userChangePassword.getPassword()), CHANGE_VM_USER_PASSWORD));
                })
                .onErrorResume(e -> {
                    if (!(e instanceof PasswordsMismatchException) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("Vm not available at the moment or user data invalid");
                    } else
                        return Mono.error(e);
                });
    }

    @Transactional
    public Mono<ProxmoxVmUsersInfo> getVmUsers(String vmId){
        return facadesUtils.getUserCodeFromContext()
                .flatMap(userCode -> facadesUtils.getVdsByUserCodeAndVmId(userCode, vmId))
                .flatMap(vds -> proxmoxUtils.sendGetRequestToCluster(Map.of("vmId", vds.getVmNumber(), "node", vds.getVmNode()), GET_VM_USERS, ProxmoxVmUsersInfo.class))
                .filter(proxmoxVmUsersInfo -> !proxmoxVmUsersInfo.getVmUsers().isEmpty())
                .switchIfEmpty(Mono.error(new LackActiveUsersException("No active users")))
                .onErrorResume(e -> {
                    if (!(e instanceof LackActiveUsersException) && !(e instanceof VmNotFoundException)) {
                        log.error(e.getMessage());
                        return facadesUtils.getServiceException("Vm not available at the moment or user data invalid");
                    } else
                        return Mono.error(e);
                });
    }


}


