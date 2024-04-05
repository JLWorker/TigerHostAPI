package tgc.plus.proxmoxservice.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxMergerNodeInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxNodeStoragesInfo;
import tgc.plus.proxmoxservice.dto.proxmox_cluster_dto.responses.nodes.ProxmoxNodesLoadInfo;
import tgc.plus.proxmoxservice.exceptions.proxmox_exceptions.proxmox_utils.ProxmoxConnectionError;
import tgc.plus.proxmoxservice.services.utils.WebClientUtils;
import tgc.plus.proxmoxservice.services.utils.paths.NodesClusterPaths;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Getter
public class ProxmoxService {

    @Value("${proxmox.requests.retries}")
    private Integer retries;

    @Value("${proxmox.requests.retries_backoff}")
    private Long retriesBackoff;

    @Value("${proxmox.balancer.delay}")
    private Integer balancerDelay;

    @Autowired
    private WebClientUtils webClientUtils;

    private Flux<List<ProxmoxMergerNodeInfo>> actualNodeInfo;

    @EventListener(value = ApplicationStartedEvent.class)
    public void startProxmoxInterviewer(){
        this.actualNodeInfo = getNodesInfo().share();
    }

    private Flux<List<ProxmoxMergerNodeInfo>> getNodesInfo(){
          return Flux.interval(Duration.ofSeconds(balancerDelay)).flatMap(el-> webClientUtils.sendGetRequestToCluster(Map.of(), NodesClusterPaths.GET_NODES_LOADS, ProxmoxNodesLoadInfo.class)
                    .flatMap(nodesInfo -> Flux.fromIterable(nodesInfo.getNodesLoads())
                        .flatMap(nodeInfo -> webClientUtils.sendGetRequestToCluster(Map.of("node", nodeInfo.getNode()), NodesClusterPaths.GET_NODE_STORAGE, ProxmoxNodeStoragesInfo.class)
                                .flatMap(storageInfo -> {
                                    long availRam = nodeInfo.getMaxRam()-nodeInfo.getRam();
                                    return Mono.just(new ProxmoxMergerNodeInfo(nodeInfo.getNode(), nodeInfo.getStatus(), nodeInfo.getCpu(), availRam, storageInfo.getNodesStorages()));
                                })).collectList())
                        .retryWhen(Retry.backoff(retries, Duration.ofMillis(retriesBackoff)))
                        .onErrorResume(e -> {
                            log.error(e.getMessage());
                            return getProxmoxConnectionError();
                        })).cache(1);
    }


    public <T> Mono<T> getProxmoxConnectionError(){
        return Mono.error(new ProxmoxConnectionError("Failed to connect to the proxmox cluster or request is invalid"));
    }
}
