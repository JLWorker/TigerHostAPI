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
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.NodesLoadsInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.VmCurrentState;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.VmDiskInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.VmPartitionDiskData;
import tgc.plus.proxmoxservice.listeners.MessageListener;
import tgc.plus.proxmoxservice.services.utils.paths.NodesClusterPaths;
import tgc.plus.proxmoxservice.services.utils.paths.VmClusterPaths;

import java.util.List;
import java.util.Map;


@SpringBootTest
public class ProxmoxUtilsTest {

    @MockBean
    MessageListener messageListener;

    @Autowired
    ProxmoxUtils proxmoxUtils;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String node = "tgc";

    private Long vmId = 102L;

    private Map<String, Object> requestVmParams = Map.of("node", node, "vmId", vmId);

    @Test
    public void sendClusterRequestToGetVmInfo(){
        proxmoxUtils.sendGetRequestToCluster(requestVmParams, VmClusterPaths.GET_VM_CURRENT, VmCurrentState.class)
                .doOnSuccess(res ->{
                    Assertions.assertNotNull(res, "Body result is empty");
                    logger.info("Machine ram station {}", res.getRam().toString());
                 })
                .doOnError(e->
                    logger.info(e.getMessage())).block();

    }

    @Test
    public void getVmDisksInfo(){
      Mono<VmDiskInfo> res =  proxmoxUtils.sendGetRequestToCluster(requestVmParams, VmClusterPaths.GET_VM_DISKS_INFO, VmDiskInfo.class);
                res.doOnSuccess(result -> {
                    Assertions.assertNotNull(result, "Body result is empty");
                    result.getVmDisks().forEach(el -> logger.info("Mount point - {}", el.getMountPoint()));
                }).block();
    }

    @Test
    public void getNodesLoads(){
        Mono<NodesLoadsInfo> res =  proxmoxUtils.sendGetRequestToCluster(requestVmParams, NodesClusterPaths.GET_NODES_LOADS, NodesLoadsInfo.class);
        res.doOnSuccess(result -> {
            Assertions.assertNotNull(result, "Body result is empty");
            result.getNodesLoads().forEach(el -> logger.info("Max system disk - {}", el.getMaxSystemDisk().toString()));
        }).block();
    }

}
