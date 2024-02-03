package tgc.plus.authservice.facades;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionTemplate;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.VersionsTypes;
import tgc.plus.authservice.dto.kafka_message_dto.KafkaMessage;
import tgc.plus.authservice.dto.kafka_message_dto.PasswordRestoreData;
import tgc.plus.authservice.dto.user_dto.UserChangeContactResponse;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.exceptions.exceptions_clases.AuthException;
import tgc.plus.authservice.exceptions.exceptions_clases.VersionException;
import tgc.plus.authservice.repository.UserRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

@Component
@Slf4j
public class FacadesUtils {

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaProducerConfig kafkaProducerConfig;

    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${tgc.web.url}")
    String recoverUrl;


    public Mono<Authentication> checkCredentials(String userCode, String password, Collection<GrantedAuthority> authority) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userCode, password, authority);
                return springSecurityConfig.reactiveLoginAuthenticationManager().authenticate(authenticationToken);
    }

    public Mono<Void> getUserByEmailReg(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() == null)
                .switchIfEmpty(getRequestException(String.format("User with email %s already exist", email)))
                .flatMap(el -> Mono.empty());
    }

    public Mono<User> getUserByEmailLog(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() != null)
                .switchIfEmpty(getRequestException(String.format("User with email %s not found", email)));
    }

    public Mono<UserChangeContactResponse> updatePhoneNumber(String phone, String userCode, Long reqVersion){
          return userRepository.changePhone(phone, userCode, reqVersion)
                .filter(newVersion -> newVersion != 0)
                  .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                          .flatMap(user -> getVersionException(user.getVersion())))
                  .flatMap(newVersion -> {
                      log.info(String.format("Phone was change for userCode %s, new phone - %s", userCode, phone));
                      return Mono.just(new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), newVersion,
                              "phone", phone)));
                  });
    }

    public Mono<UserChangeContactResponse> updateEmail(String email, String userCode, Long reqVersion){
        return userRepository.changeEmail(email, userCode, reqVersion)
                .filter(newVersion -> newVersion != 0)
                .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                        .flatMap(user -> getVersionException(user.getVersion())))
                .flatMap(newVersion -> {
                            log.info(String.format("Email was change for userCode %s, new email - %s", userCode, email));
                            return Mono.just(new UserChangeContactResponse(Map.of(VersionsTypes.USER_VERSION.getName(), newVersion,
                                    "email", email)));
                });
    }

//    public Mono<UserInfoResponse> getInfo(String userCode, String regVersion){
//        return userRepository.getUserByUserCode(userCode)
//                .flatMap(user -> {
//                    if (!user.getVersion().equals(regVersion))
//                        return getVersionException(user.getVersion());
//                    else
//                        return Mono.just(new UserInfoResponse(user.getPhone(), user.getEmail(),
//                                user.getTwoAuthStatus(),user.getVersion()))
//                })
//    }
//
    public Mono<String> generateToken(Long version, String userCode){
        return Mono.defer(()->{
            String recoveryCode = UUID.randomUUID().toString();
            Instant expiredDate = Instant.now().plus(Duration.ofHours(1));
            return userRepository.changeRecoveryCode(userCode, recoveryCode, expiredDate, version)
                    .filter(newVersion -> newVersion != 0)
                    .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                            .flatMap(user -> getVersionException(user.getVersion())))
                    .flatMap(newVersion -> {
                        log.info(String.format("For user %s created recover code", userCode));
                        return Mono.just(recoveryCode);
                    });
        });
    }

    public Mono<KafkaMessage> createMessageForRestorePsw(String token, String userCode){
        return Mono.defer(()->{
            PasswordRestoreData passwordRestoreData = new PasswordRestoreData(recoverUrl+token);
            return Mono.just(new KafkaMessage(userCode, passwordRestoreData));
        });
    }


    public Mono<Void> sendMessageInCallService(KafkaMessage message){
        return Mono.defer(() -> {
            ProducerRecord<Long, KafkaMessage> record = new ProducerRecord<>(topic, message);
            CompletableFuture<SendResult<Long, KafkaMessage>> future = kafkaProducerConfig.kafkaTemplate().send(record);
            return Mono.fromFuture(future).then(Mono.empty());
        });
    }

    private <T> Mono<T> getVersionException(Long version){
        return Mono.error(new VersionException(version, "Version incorrect"));
    }


    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

    private  <T> Mono<T> getUserNotFoundException(String message){
        return Mono.error(new UsernameNotFoundException(message));
    }

}
