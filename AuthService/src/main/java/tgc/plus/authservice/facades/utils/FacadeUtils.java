package tgc.plus.authservice.facades.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.kafka_message_dto.*;
import tgc.plus.authservice.dto.tokens_dto.UpdateTokenResponse;
import tgc.plus.authservice.dto.user_dto.UserChangeContacts;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.entity.UserToken;
import tgc.plus.authservice.exceptions.exceptions_clases.*;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenService;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class FacadeUtils {

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    KafkaProducerConfig kafkaProducerConfig;

    @Autowired
    TokenService tokenService;

    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${tgc.web.url}")
    String recoverUrl;


    public Mono<Authentication> checkCredentials(String userCode, String password, Collection<GrantedAuthority> authority) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userCode, password, authority);
                return springSecurityConfig.reactiveLoginAuthenticationManager().authenticate(authenticationToken);
    }

    public Mono<Boolean> checkPasswords(String password, String confirmPassword){
        return Mono.defer(() -> {
            if (password.equals(confirmPassword)){
                return Mono.just(true);
            }
            else
                return getRequestException("Passwords mismatch");
        });
    }

    public Mono<Void> getUserByEmailReg(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() == null)
                .switchIfEmpty(getRequestException(String.format("User with email %s already exist", email)))
                .flatMap(el -> Mono.empty());
    }

    public Mono<User> getUserByRecoveryCode(String recoveryCode) {
        return userRepository.getUserByRecoveryCode(recoveryCode)
                .defaultIfEmpty(new User())
                .filter(el -> el.getUserCode() != null)
                .switchIfEmpty(getRequestException("Recovery code invalid"));
    }

    public Mono<User> getUserByEmailLog(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() != null)
                .switchIfEmpty(getRequestException(String.format("User with email %s not found", email)));
    }

    Mono<Void> getUserTokenByTokenId(String tokenId){
        return userTokenRepository.getUserTokenByTokenId(tokenId)
                .defaultIfEmpty(new UserToken())
                .filter(userToken -> userToken.getId()!=null)
                .switchIfEmpty(getRefreshTokenException(String.format("Token with id %s does not exist", tokenId)))
                .flatMap(res -> Mono.empty());
    }

    public Mono<UserToken> getRefreshToken(String refreshToken){
        return userTokenRepository.getUserTokenByRefreshToken(refreshToken)
                .defaultIfEmpty(new UserToken())
                .filter(userToken -> userToken.getTokenId() != null)
                .switchIfEmpty(getRefreshTokenNotFoundException("Token not exist"))
                .flatMap(Mono::just);
    }

    public Mono<Long> updatePhoneNumber(String phone, String userCode, Long reqVersion){
          return userRepository.changePhone(phone, userCode, reqVersion)
                .filter(newVersion -> newVersion != 0)
                  .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                          .flatMap(user -> getVersionException(user.getVersion())))
                  .flatMap(newVersion -> {
                      log.info(String.format("Phone was change for userCode %s, new phone - %s", userCode, phone));
                      return Mono.just(newVersion);
                  });

    }

    public Mono<Long> updateEmail(String email, String userCode, Long reqVersion){
        return userRepository.changeEmail(email, userCode, reqVersion)
                .filter(newVersion -> newVersion != 0)
                .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                        .flatMap(user -> getVersionException(user.getVersion())))
                .flatMap(newVersion -> {
                            log.info(String.format("Email was change for userCode %s, new email - %s", userCode, email));
                            return Mono.just(newVersion);
                });
    }

    public Mono<Void> changePassword(Long version, String password, Instant expiredDate, String userCode){
            if (expiredDate.isBefore(Instant.now())) {
                return userRepository.clearRecoveryCode(userCode, version)
                        .flatMap(newVersion -> Mono.empty())
                        .then(Mono.error(new RecoveryCodeExpiredException("Recovery code expired")));//send new version to users;
            }
            else {
                String bcryptPassword = springSecurityConfig.bCryptPasswordEncoder().encode(password);
                return userRepository.changePassword(userCode, bcryptPassword, version)
                        .filter(newVersion -> newVersion!=0)
                        .switchIfEmpty(getRequestException("Password already change"))
                        .flatMap(newVersion -> Mono.empty()); //send new version

            }
    }

    public Mono<Void> removeUserToken(String tokenId){
        return userTokenRepository.removeUserTokenByTokenId(tokenId)
                .filter(result -> result!=0)
                .switchIfEmpty(getRefreshTokenNotFoundException("Token already remove"))
                .flatMap(res -> Mono.empty());
    }

    public Mono<Void> removeAllUserTokens(String tokenId, String userCode){
        return getUserTokenByTokenId(tokenId)
                .then(userRepository.getUserByUserCode(userCode)
                        .flatMap(user -> userTokenRepository.removeAllUserTokens(tokenId, user.getId())
                                .filter(result -> result!=0)
                                .switchIfEmpty(getRefreshTokenException("Tokens already remove"))
                                .flatMap(res -> Mono.empty())));
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
    public Mono<String> generateRecoveryCode(Long version, String userCode){
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

    public Mono<Map<String, String>> updatePairTokens(String refreshToken, Long userId, Boolean checkResult){
        if (!checkResult) {
            return userTokenRepository.removeUserTokenByRefreshToken(refreshToken)
                    .then(getRefreshTokenException("Refresh token expired"));
        }
        else
            return userRepository.getUserById(userId)
                            .flatMap(user -> tokenService.updateAccessTokenMobile(refreshToken, user.getUserCode(), user.getRole()));
    }


    public Mono<KafkaMessage> createMessageForRestorePsw(String token, String userCode){
        return Mono.defer(()->{
            PasswordRestoreData passwordRestoreData = new PasswordRestoreData(recoverUrl+token);
            return Mono.just(new KafkaMessage(userCode, passwordRestoreData));
        });
    }

    public Mono<KafkaMessage> createMessageForSaveUser(String userCode, String email, String password){
        return Mono.defer(()->{
            SaveUserData saveUserData = new SaveUserData(email, password);
            return Mono.just(new KafkaMessage(userCode, saveUserData));
        });
    }

    public Mono<KafkaMessage> createMessageForContactUpdate(String userCode, String contact, String type){
        return Mono.defer(()->{
            if (type.equals(UserChangeContacts.ChangeEmail.class.getName())){
                EditEmailData editEmailData = new EditEmailData(contact);
                return Mono.just(new KafkaMessage(userCode, editEmailData));
            }
            else {
                EditPhoneData editPhoneData = new EditPhoneData(contact);
                return Mono.just(new KafkaMessage(userCode, editPhoneData));
            }
        });
    }


    public Mono<Void> sendMessageInCallService(KafkaMessage message, String method){
            ProducerRecord<Long, KafkaMessage> record = new ProducerRecord<>(topic, message);
            record.headers().add("method", method.getBytes());
            CompletableFuture<SendResult<Long, KafkaMessage>> future = kafkaProducerConfig.kafkaTemplate().send(record);
            return Mono.fromFuture(future)
                    .onErrorResume(e -> Mono.error(new KafkaException(e.getMessage())))
                    .then(Mono.empty());
    }

    private <T> Mono<T> getVersionException(Long version){
        return Mono.error(new VersionException(version, "Version incorrect"));
    }

    private <T> Mono<T> getRefreshTokenException(String message){
        return Mono.error(new RefreshTokenException(message));
    }

    private <T> Mono<T> getRefreshTokenNotFoundException(String message){
        return Mono.error(new RefreshTokenNotFoundException(message));
    }

    private <T> Mono<T> getSimpleVersionException(String message){
        return Mono.error(new VersionException(message));
    }


    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }


    private  <T> Mono<T> getUserNotFoundException(String message){
        return Mono.error(new UsernameNotFoundException(message));
    }

}
