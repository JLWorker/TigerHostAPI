package tgc.plus.authservice.facades.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.SenderRecord;
import reactor.util.function.Tuple2;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.dto.jwt_claims_dto.AccessTokenClaimsDto;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessageDto;
import tgc.plus.authservice.dto.kafka_message_dto.headers.KafkaHeadersDto;
import tgc.plus.authservice.dto.user_dto.DeviceDataDto;
import tgc.plus.authservice.entities.TokenMeta;
import tgc.plus.authservice.entities.User;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.RefreshTokenException;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.InvalidRequestException;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.NotFoundException;
import tgc.plus.authservice.exceptions.exceptions_elements.service_exceptions.ServerException;
import tgc.plus.authservice.facades.utils.utils_enums.PartitioningStrategy;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.utils.utils_enums.TokenType;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FacadeUtils {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final TokenService tokenService;
    private final TokenMetaRepository tokenMetaRepository;
    private final ObjectMapper objectMapper;
    private final UserTokenRepository userTokenRepository;

    @Value("${login.max-tokens}")
    private Integer maxTokens;

    public Mono<Void> checkPasswords(String password, String confirmPassword){
       return (password.equals(confirmPassword)) ? Mono.empty() : getInvalidRequestException("Passwords mismatch");
    }

    public Mono<String> getPrincipal(){
        return Mono.defer(() -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication().getPrincipal().toString()));
    }
    public Mono<Void> sendMessageInKafkaTopic(KafkaMessageDto message, String topic, KafkaHeadersDto kafkaHeadersDTO, PartitioningStrategy strategy){
        ProducerRecord<String, KafkaMessageDto> messageRecord = new ProducerRecord<>(topic, strategy.getStrategy(), message);
        Map<String, String> headers = objectMapper.convertValue(kafkaHeadersDTO, new TypeReference<>() {});
        headers.forEach((key, value) -> messageRecord.headers().add(key, value.getBytes()));
        SenderRecord<String, KafkaMessageDto, String> senderRecord = SenderRecord.create(messageRecord, UUID.randomUUID().toString());
        return kafkaProducerConfig.kafkaSender().send(Mono.just(senderRecord)).then();
    }

    public Mono<Void> checkTokensCount(String userCode){
        return userTokenRepository.getBlockForAllUserTokens(userCode).collectList()
                .filter(list -> list.size()>=maxTokens)
                .switchIfEmpty(Mono.empty())
                .flatMap(list -> Mono.just(list.stream().sorted().findFirst().get()))
                .flatMap(userToken -> userTokenRepository.deleteUserTokenByRefreshToken(userToken.getRefreshToken()));
    }

    public Mono<Tuple2<String, UserToken>> generatePairTokens(User user, DeviceDataDto deviceDataDto, String ipAddr){
        return tokenService.createAccessToken(new AccessTokenClaimsDto(user.getUserCode(), user.getRole()), TokenType.SECURITY)
                .zipWith(tokenService.createRefToken(user.getId()))
                .flatMap(tokens -> tokenMetaRepository.save(new TokenMeta(tokens.getT2().getId(), ipAddr, deviceDataDto.getName(), deviceDataDto.getApplicationType()))
                        .thenReturn(tokens));
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
