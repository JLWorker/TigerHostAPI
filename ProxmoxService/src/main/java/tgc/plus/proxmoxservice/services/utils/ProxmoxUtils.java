package tgc.plus.proxmoxservice.services.utils;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.ObjectMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.configs.FluxConfig;
import tgc.plus.proxmoxservice.exceptions.proxmox_exceptions.web_client.UnexpectedJsonNodeType;
import tgc.plus.proxmoxservice.services.utils.paths.ClusterPaths;
import java.util.Map;

@Component
@Slf4j
public class ProxmoxUtils {

    @Autowired
    private FluxConfig fluxConfig;

    @Autowired
    private ObjectMapper mapper;


    public <T> Mono<T> sendGetRequestToCluster(Map<String, Object> params, ClusterPaths path, Class<T> expectedResponse){
        return fluxConfig.webClient()
                .get()
                .uri(path.getUrl(), params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> getDataFromResponse(json, expectedResponse));
    }

    public Mono<Void> sendChangeRequestToCluster(HttpMethod httpMethod, Map<String, Object> params, Object body, ClusterPaths path){
        return fluxConfig.webClient()
                .method(httpMethod)
                .uri(path.getUrl(), params)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @SneakyThrows
    private <T> Mono<T> getDataFromResponse(String json, Class<T> expectedResponse) {

        if (!json.isBlank()) {
            JsonNode nodes = mapper.readTree(json);
            JsonNode mainNode = nodes.get("data");

            if (mainNode.getNodeType().equals(JsonNodeType.ARRAY))
                return Mono.just(mapper.readValue(json, expectedResponse));
            else
                return Mono.just(mapper.readValue(mainNode.asText(), expectedResponse));
        } else
            return getUnexpectedJsonNode("Json is empty");
    }

    private <T> Mono<T> getUnexpectedJsonNode(String message){
        return Mono.error(new UnexpectedJsonNodeType(message));
    }

}
