package tgc.plus.proxmoxservice.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.configs.FluxConfig;
import tgc.plus.proxmoxservice.services.utils.paths.ClusterPaths;

@Component
public class ProxmoxUtils {

    @Autowired
    private FluxConfig fluxConfig;

    public Mono<Object> sendRequestToCluster(HttpMethod method, Object params, ClusterPaths path, Class<Object> responseClass){
        return fluxConfig.webClient()
                .method(method)
                .uri(path.getUrl(), params)
                .retrieve()
                .bodyToMono(responseClass);
    }

}
