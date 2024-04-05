package tgc.plus.proxmoxservice.facades.utils;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.vm_controller_dto.responses.UserAllVms;
import tgc.plus.proxmoxservice.entities.Vds;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.ServiceException;
import tgc.plus.proxmoxservice.exceptions.facades_exceptions.vm.VmNotFoundException;
import tgc.plus.proxmoxservice.repositories.VdsRepository;
import tgc.plus.proxmoxservice.repositories.db_client_repository.CustomDatabaseClientRepository;

import java.security.MessageDigest;
import java.security.Security;
import java.util.Base64;

@Component
@Slf4j
public class FacadesUtils {


    @Autowired
    private CustomDatabaseClientRepository customRepository;

    @Autowired
    private VdsRepository vdsRepository;

    public Mono<UserAllVms> getAllUserVms(String userCode) {
        return customRepository.getAllUserVms(userCode)
                .collectList()
                .flatMap(list -> Mono.just(new UserAllVms(list)));
    }

    public Mono<Vds> getVdsByUserCodeAndVmId(String userCode, String vmId){
        return vdsRepository.getVdsByUserCodeAndVmId(userCode, vmId)
                .defaultIfEmpty(new Vds())
                .filter(vds -> vds.getId() != null)
                .switchIfEmpty(getVmNotFoundException(String.format("Virtual machine with id: %s not exist", vmId)))
                .flatMap(Mono::just);
    }

    public Mono<String> getUserCodeFromContext(){
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext ->
                    Mono.just((String) securityContext.getAuthentication().getPrincipal()));
    }

    public <T> Mono<T> getServiceException(String message){
        return Mono.error(new ServiceException(message));
    }

    public <T> Mono<T> getVmNotFoundException(String message){
        return Mono.error(new VmNotFoundException(message));
    }
 }

