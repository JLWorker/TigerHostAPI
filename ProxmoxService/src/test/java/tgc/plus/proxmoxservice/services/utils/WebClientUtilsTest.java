package tgc.plus.proxmoxservice.services.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests.ProxmoxVmClone;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.requests.ProxmoxVmUserSetPassword;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxNodesLoadInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmCurrentState;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmDiskInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.vm.ProxmoxVmUsersInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxNodeStoragesInfo;
import tgc.plus.proxmoxservice.listeners.MessageListener;
import tgc.plus.proxmoxservice.services.utils.paths.NodesClusterPaths;
import tgc.plus.proxmoxservice.services.utils.paths.VmClusterPaths;

import java.util.Map;
import java.util.Random;


@SpringBootTest
public class WebClientUtilsTest {

    @MockBean
    MessageListener messageListener;

    @Autowired
    WebClientUtils webClientUtils;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String node = "tgc";

    private final Long vmId = 179L;

    private final Map<String, Object> requestVmParams = Map.of("node", node, "vmId", vmId);

    @Test
    public void sendClusterRequestToGetVmInfo(){

        webClientUtils.sendGetRequestToCluster(requestVmParams, VmClusterPaths.GET_VM_CURRENT, ProxmoxVmCurrentState.class)
                .doOnSuccess(res ->{
                    Assertions.assertNotNull(res, "Body result is empty");
                    logger.info("Machine ram station {}", res.getRam().toString());
                 })
                .doOnError(e->
                    logger.info(e.getMessage())).block();

    }

    @Test
    public void getVmDisksInfo(){
      Mono<ProxmoxVmDiskInfo> res =  webClientUtils.sendGetRequestToCluster(requestVmParams, VmClusterPaths.GET_VM_DISKS_INFO, ProxmoxVmDiskInfo.class);
                res.doOnSuccess(result -> {
                    Assertions.assertNotNull(result, "Body result is empty");
                    result.getVmDisks().forEach(el -> logger.info("Mount point - {}", el.getMountPoint()));
                }).block();
    }

    @Test
    public void getNodesLoads(){
        Mono<ProxmoxNodesLoadInfo> res = webClientUtils.sendGetRequestToCluster(requestVmParams, NodesClusterPaths.GET_NODES_LOADS, ProxmoxNodesLoadInfo.class);
        res.doOnSuccess(result -> {
            Assertions.assertNotNull(result, "Body result is empty");
            result.getNodesLoads().forEach(el -> logger.info("Max system disk - {}", el.getMaxRam().toString()));
        }).block();
    }

    @Test
    public void getNodesStorageInfo(){
        Mono<ProxmoxNodeStoragesInfo> res = webClientUtils.sendGetRequestToCluster(requestVmParams, NodesClusterPaths.GET_NODE_STORAGE, ProxmoxNodeStoragesInfo.class);
        res.doOnSuccess(result -> {
            Assertions.assertNotNull(result, "Body result is empty");
            result.getNodesStorages().forEach(el -> logger.info("Max system disk - {}", el.getStorage()));
        }).block();
    }

    @Test
    public void getVmUsers() {
        Mono<ProxmoxVmUsersInfo> res = webClientUtils.sendGetRequestToCluster(requestVmParams, VmClusterPaths.GET_VM_USERS, ProxmoxVmUsersInfo.class);
        res.doOnSuccess(result -> {
            Assertions.assertFalse(result.getVmUsers().isEmpty(), "Body result is empty");
            result.getVmUsers().forEach(el -> logger.info("User - {}", el.getUser()));
        }).block();
    }

    @Test
    public void createVmClone(){
        Long newVmId = new Random().nextLong(105, 199);
        ProxmoxVmClone newClone = new ProxmoxVmClone(newVmId, true);
        Map<String, Object> params = Map.of("node", node, "vmId", 100);
        webClientUtils.sendChangeRequestToCluster(HttpMethod.POST, params, newClone, VmClusterPaths.CLONE_VM)
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success send request, vm - {}", newVmId))
                .block();
    }

    @Test
    public void changePassword(){
        ProxmoxVmUserSetPassword userSetPassword = new ProxmoxVmUserSetPassword("root", "12345");
        webClientUtils.sendChangeRequestToCluster(HttpMethod.POST, requestVmParams, userSetPassword, VmClusterPaths.CHANGE_VM_USER_PASSWORD)
                .doOnError(e->logger.info(e.getMessage()))
                .doOnSuccess(res -> logger.info("Success send new password for user - {}", userSetPassword.getUsername()))
                .block();
    }

//    @Test
//    public void getUsersInfo(){
//        VmClone newClone = new VmClone(new Random().nextLong(105, 199), true);
//        Map<String, Object> params = Map.of("node", node, "vmId", 100);
//        proxmoxUtils.sendChangeRequestToCluster(HttpMethod.POST, params, newClone, VmClusterPaths.CLONE_VM)
//                .block();
//    }



}
