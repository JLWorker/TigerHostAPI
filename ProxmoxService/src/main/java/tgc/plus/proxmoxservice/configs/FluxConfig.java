package tgc.plus.proxmoxservice.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;

@Configuration
@EnableWebFlux
public class FluxConfig {

    @Value("${flux.client.selectorCount}")
    private Integer selectorCount;

    @Value("${flux.client.workerCount}")
    private Integer workerCount;

    @Value("${flux.client.maxConnections}")
    private Integer maxConnections;

    @Value("${flux.client.pendingAcquireMaxCount}")
    private Integer pendingAcquireMaxCount;

    @Value("${flux.client.responseTimeout}")
    private Integer responseTimeout;

    @Value("${proxmox.accessKey}")
    private String accessKey;

    @Value("${proxmox.url.path}")
    private String clusterBaseUrl;

    @Bean
    public ClientHttpConnector httpConnector(){

        String prefix = "proxmox-client";

        LoopResources loopResource = (workerCount.equals(-1) || selectorCount.equals(-1)) ?
                LoopResources.create(prefix):
                LoopResources.create(prefix, selectorCount, workerCount, true);

        ReactorResourceFactory resourceFactory = new ReactorResourceFactory();
                resourceFactory.setLoopResources(loopResource);

        ConnectionProvider connectionProvider = ConnectionProvider.builder(prefix)
                .maxConnections((maxConnections.equals(-1)) ? ConnectionProvider.DEFAULT_POOL_MAX_CONNECTIONS : maxConnections)
                .pendingAcquireMaxCount((pendingAcquireMaxCount.equals(0)) ? maxConnections*2 : pendingAcquireMaxCount)
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofMillis(responseTimeout))
                .baseUrl(clusterBaseUrl);

        return new ReactorClientHttpConnector(httpClient);
    }

    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .clientConnector(httpConnector())
                .defaultHeader("Authorization", accessKey)
                .build();
    }

}
