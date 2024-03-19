package tgc.plus.proxmoxservice.services.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import jakarta.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.proxmoxservice.configs.FluxConfig;
import tgc.plus.proxmoxservice.exceptions.proxmox_exceptions.UnexpectedJsonNodeType;
import tgc.plus.proxmoxservice.services.utils.paths.ClusterPaths;

import java.util.Map;

@Component
@Slf4j
public class ProxmoxUtils {

    @Autowired
    private FluxConfig fluxConfig;

    @Autowired
    private ObjectMapper objectMapper;

    public <T> Mono<T> sendGetRequestToCluster(Map<String, Object> params, ClusterPaths path, Class<T> expectedResponse){
        return fluxConfig.webClient()
                .get()
                .uri(path.getUrl(), params)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> getDataFromResponse(json, expectedResponse));
    }

    public Mono<Void> sendChangeRequestToCluster(HttpMethod httpMethod, Object body, ClusterPaths path){
        return fluxConfig.webClient()
                .method(httpMethod)
                .uri(path.getUrl())
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    public <T> Mono<T> getDataFromResponse(String json, Class<T> expectedResponse){
        try {
            JsonNode nodes = objectMapper.readTree(json);
            JsonNode data = nodes.get("data");

            if (data!=null) {
                if (data.getNodeType().equals(JsonNodeType.ARRAY))
                    return Mono.just(objectMapper.readValue(json, expectedResponse));

                else if (data.getNodeType().equals(JsonNodeType.OBJECT))
                    return Mono.just(objectMapper.treeToValue(data, expectedResponse));

                else
                    return Mono.error(new UnexpectedJsonNodeType(String.format("First json node have type - %s", data.getNodeType())));
            }
            else
                return getNullPointerException("Data in json is null");

        } catch (JsonProcessingException | RuntimeException e) {
            return Mono.error(e);
        }
    }

    public <T> Mono<T> getNullPointerException(String message){
        return Mono.error(new NullPointerException(message));
    }

}
