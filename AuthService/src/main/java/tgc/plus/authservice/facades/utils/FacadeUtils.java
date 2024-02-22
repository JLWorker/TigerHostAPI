package tgc.plus.authservice.facades.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.KafkaProducerConfig;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.dto.kafka_message_dto.*;
import tgc.plus.authservice.dto.kafka_message_dto.message_payloads.*;
import tgc.plus.authservice.dto.two_factor_dto.QrCodeData;
import tgc.plus.authservice.dto.two_factor_dto.TwoFactorSwitchResponse;
import tgc.plus.authservice.dto.user_dto.DeviceData;
import tgc.plus.authservice.dto.user_dto.TokensResponse;
import tgc.plus.authservice.dto.user_dto.UserChangeContacts;
import tgc.plus.authservice.entity.TwoFactor;
import tgc.plus.authservice.entity.User;
import tgc.plus.authservice.entity.UserToken;
import tgc.plus.authservice.exceptions.exceptions_clases.*;
import tgc.plus.authservice.repository.TwoFactorRepository;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.TwoFactorService;
import tgc.plus.authservice.services.utils.TokensList;

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
    TokenMetaService tokenMetaService;

    @Autowired
    TokenService tokenService;

    @Autowired
    TwoFactorService twoFactorService;

    @Autowired
    TwoFactorRepository twoFactorRepository;

    @Value("${spring.kafka.topic}")
    String topic;

    @Value("${tgc.web.url}")
    String recoverUrl;


    public Mono<Authentication> checkCredentials(String userCode, String password, Collection<GrantedAuthority> authority) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userCode, password, authority);
                return springSecurityConfig.reactiveLoginAuthenticationManager().authenticate(authenticationToken);
    }

    public Mono<Void> checkPasswords(String password, String confirmPassword){
            if (password.equals(confirmPassword)){
                return Mono.empty();
            }
            else
                return getRequestException("Passwords mismatch");
    }

    public Mono<TokensResponse> verify2FaCode(String deviceToken, String code, DeviceData deviceData, String ipAddr){
        return twoFactorRepository.getTwoFactorByDeviceToken(deviceToken)
                .defaultIfEmpty(new TwoFactor())
                .filter(twoFactor -> twoFactor.getId() != null)
                .switchIfEmpty(Mono.error(new TwoFactorTokenException("Invalid 2fa token")))
                .flatMap(twoFactor -> userRepository.getUserById(twoFactor.getUserId()))
                .flatMap(user -> twoFactorService.verify(code, user.getTwoFactorSecret())
                        .filter(res -> res)
                        .switchIfEmpty(Mono.error(new TwoFactorCodeException("Authentication code is incorrect")))
                        .then(twoFactorRepository.removeTwoFactorByDeviceToken(deviceToken)
                                .then(generatePairTokens(user, deviceData, ipAddr))));
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

    Mono<UserToken> getUserTokenByTokenId(String tokenId){
        return userTokenRepository.getUserTokenByTokenId(tokenId)
                .filter(userToken -> userToken.getUserId() != null)
                .switchIfEmpty(getRefreshTokenNotFoundException(String.format("Token with id %s does not exist", tokenId)));
    }

    public Mono<UserToken> getRefreshToken(String refreshToken){
        return userTokenRepository.getUserTokenByRefreshToken(refreshToken)
                .filter(userToken -> userToken.getTokenId() != null)
                .switchIfEmpty(getRefreshTokenException("Token not exist"))
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

    public Mono<TwoFactorSwitchResponse> switch2FaStatus(String userCode, Long version){
        return userRepository.getUserByUserCode(userCode)
                .flatMap(user ->{
                    if (user.getTwoAuthStatus())
                            return change2FaStatus(userCode, false, version)
                                    .flatMap(newVersion -> Mono.just(new TwoFactorSwitchResponse(newVersion)));
                    else
                        if (user.getTwoFactorSecret()!=null)
                            return change2FaStatus(userCode, true, version)
                                    .flatMap(newVersion -> Mono.just(new TwoFactorSwitchResponse(newVersion)));
                        else
                            return twoFactorService.generateSecret()
                                    .flatMap(secret -> userRepository.updateTwoFactorSecret(user.getUserCode(), secret, version)
                                    .filter(newVersion -> newVersion!=0)
                                    .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                                            .flatMap(updateUser -> getVersionException(updateUser.getVersion())))
                                    .flatMap(newVersion -> Mono.just(new TwoFactorSwitchResponse(newVersion))));
                });
    }


    public Mono<Long> change2FaStatus(String userCode, Boolean status, Long version){
        return userRepository.switchTwoFactorStatus(userCode, status, version)
                            .filter(newVersion -> newVersion!=0)
                            .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                                .flatMap(user -> getVersionException(user.getVersion())));
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
                        .switchIfEmpty(getRequestException("Password already changed"))
                        .flatMap(newVersion -> Mono.empty()); //отослать новые версии подписчикам

            }
    }

    public Mono<Void> removeUserToken(String tokenId){
        return userTokenRepository.removeUserTokenByTokenId(tokenId)
                .onErrorResume(e->getRefreshTokenNotFoundException("Token already removed"))
                .filter(res -> res!=0)
                .switchIfEmpty(getRefreshTokenNotFoundException("Token already removed"))
                .flatMap(res -> Mono.empty());
    }

    public Mono<Void> removeAllUserTokens(String tokenId){
        return getUserTokenByTokenId(tokenId)
                        .flatMap(userToken -> userTokenRepository.removeAllUserTokens(tokenId, userToken.getUserId())
                                .onErrorResume(e->getRefreshTokenNotFoundException("Tokens already removed"))
                                .flatMap(res -> Mono.empty()));
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


    public Mono<QrCodeData> generateQrCode(String userCode){
        return userRepository.getUserByUserCode(userCode)
                .filter(user -> user.getTwoFactorSecret()!=null)
                .switchIfEmpty(Mono.error(new SecretNotFoundException("Secret code not found for user, enable two factor status")))
                .flatMap(user -> twoFactorService.generateQrCode(user.getEmail(), user.getTwoFactorSecret()));
    }

    public Mono<String> generate2FaDeviceToken(Long userId){
        String deviceToken = UUID.randomUUID().toString();
        return twoFactorRepository.save(new TwoFactor(userId, deviceToken, Instant.now()))
                .flatMap(user -> tokenService.createAccessToken(Map.of("device_token", deviceToken), TokensList.TWO_FACTOR.getName()));
    }

    public Mono<TokensResponse> generatePairTokens(User user, DeviceData deviceData, String ipAddr){
        return tokenService.createAccessToken(Map.of("user_code", user.getUserCode(), "role", user.getRole()), TokensList.SECURITY.getName())
                .zipWith(tokenService.createRefToken(user.getId()))
                .flatMap(tokens -> tokenMetaService.save(deviceData, tokens.getT2().getId(), ipAddr)
                        .flatMap(tokenMeta -> {
                            log.info(String.format("User with email - %s, logged, tokens was created", user.getEmail() ));
                            return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(), user.getVersion()));
                        }));
    }


    public Mono<Map<String, Object>> updatePairTokens(String refreshToken, Long userId, Boolean checkResult){
        if (!checkResult) {
            return userTokenRepository.removeUserTokenByRefreshToken(refreshToken)
                    .then(getRefreshTokenException("Token expired"));
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

//    public Mono<KafkaMessage> createMessageForTwoFactorCode(Integer code, String userCode){
//        return Mono.defer(()->{
//            TwoAuthData twoAuthData = new TwoAuthData(code);
//            return Mono.just(new KafkaMessage(userCode, twoAuthData));
//        });
//    }

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

//    private <T> Mono<T> getInvalidTwoFactorCodeException(String message, HttpStatus httpStatus, Map<String, String> headers){
//        if (headers.isEmpty())
//         return Mono.error(new TwoFactorCodeException(message, httpStatus));
//        else
//            return Mono.error(new TwoFactorCodeException(message, httpStatus, headers));
//    }

    private <T> Mono<T> getRefreshTokenException(String message){
        return Mono.error(new RefreshTokenException(message));
    }

    private <T> Mono<T> getRefreshTokenNotFoundException(String message){
        return Mono.error(new RefreshTokenNotFoundException(message));
    }

    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }



}
