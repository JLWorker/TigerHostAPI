package tgc.plus.authservice.facades.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import reactor.util.function.Tuple2;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaims;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;
import tgc.plus.authservice.dto.kafka_message_dto.headers.KafkaHeadersDTO;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.entities.TokenMeta;
import tgc.plus.authservice.entities.User;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.*;
import tgc.plus.authservice.facades.utils.utils_enums.CookiePayload;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.utils.utils_enums.TokenType;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class FacadeUtils {

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenMetaRepository tokenMetaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${cookie.ref.domain}")
    private String refCookieDomain;

    public Mono<Void> checkPasswords(String password, String confirmPassword){
       return (password.equals(confirmPassword)) ? Mono.empty() : getInvalidRequestException("Passwords mismatch");
    }


    public Mono<String> getPrincipal(){
        return Mono.defer(() -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal().toString()));
    }
    public Mono<Void> sendMessageInKafkaTopic(KafkaMessage message, String topic, KafkaHeadersDTO kafkaHeadersDTO, PartitioningStrategy strategy){
        ProducerRecord<String, KafkaMessage> messageRecord = new ProducerRecord<>(topic, strategy.getStrategy(), message);
        Map<String, String> headers = objectMapper.convertValue(kafkaHeadersDTO, new TypeReference<>() {});
        headers.forEach((key, value) -> messageRecord.headers().add(key, value.getBytes()));
        SenderRecord<String, KafkaMessage, String> senderRecord = SenderRecord.create(messageRecord, UUID.randomUUID().toString());
        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord)).then();
    }

        public Mono<Tuple2<String, UserToken>> generatePairTokens(User user, DeviceData deviceData, String ipAddr){
        return tokenService.createAccessToken(new AccessTokenClaims(user.getUserCode(), user.getRole()), TokenType.SECURITY)
                .zipWith(tokenService.createRefToken(user.getId()))
                .flatMap(tokens -> tokenMetaRepository.save(new TokenMeta(tokens.getT2().getId(), ipAddr, deviceData.getName(), deviceData.getApplicationType()))
                        .thenReturn(tokens));
    }

    public Mono<Void> addRefCookie(ServerHttpResponse serverHttpResponse, UserToken userToken){
        ResponseCookie cookie = ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name(), userToken.getRefreshToken())
                        .httpOnly(true)
                        .domain(refCookieDomain)
                        .path("/api/tokens/")
                        .secure(true)
                        .sameSite("Lax")
                        .maxAge(Duration.between(userToken.getCreateDate(), userToken.getExpiredDate()))
                        .build();
        serverHttpResponse.addCookie(cookie);
        return Mono.empty();
    }


    public <T> Mono<T> getRefreshTokenException(){
        return Mono.error(new RefreshTokenException("Refresh token not exist"));
    }

    public <T> Mono<T> getNotFoundException(String message){
        return Mono.error(new NotFoundException(message));
    }

    public <T> Mono<T> getServerException(){
        return Mono.error(new ServerException("The server is currently unable to complete the request"));
    }

    public <T> Mono<T> getInvalidRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

}
