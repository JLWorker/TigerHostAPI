package tgc.plus.authservice.facades.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import tgc.plus.authservice.entity.*;
import tgc.plus.authservice.exceptions.exceptions_clases.*;
import tgc.plus.authservice.repository.TokenMetaRepository;
import tgc.plus.authservice.repository.TwoFactorRepository;
import tgc.plus.authservice.repository.UserRepository;
import tgc.plus.authservice.repository.UserTokenRepository;
import tgc.plus.authservice.services.TokenMetaService;
import tgc.plus.authservice.services.TokenService;
import tgc.plus.authservice.services.TwoFactorService;
import tgc.plus.authservice.services.utils.TokensList;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
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

    @Autowired
    TokenMetaRepository tokenMetaRepository;

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

    public Mono<TokenMeta> checkIpAddress(String ipAddress, Long tokenId){
        return tokenMetaRepository.getTokenMetaByTokenId(tokenId)
                .filter(tokenMeta -> tokenMeta.getDeviceIp().equals(ipAddress))
                .switchIfEmpty(tokenMetaRepository.updateIpAddress(ipAddress, tokenId));
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
                .switchIfEmpty(getNotFoundException("Password already changed"));
    }

    public Mono<User> getUserByEmailLog(String email) {
        return userRepository.getUserByEmail(email).defaultIfEmpty(new User())
                .filter(el -> el.getId() != null)
                .switchIfEmpty(getNotFoundException(String.format("User with email %s not found", email)));
    }

    public Mono<UserToken> getUserTokenByTokenId(String tokenId, boolean forUpd){

        Mono<UserToken> userTokenMono = null;

        if (!forUpd)
            userTokenMono = userTokenRepository.getUserTokenByTokenId(tokenId);
        else
            userTokenMono = userTokenRepository.getUserTokenByTokenIdForShare(tokenId);

        return userTokenMono
                .defaultIfEmpty(new UserToken())
                .filter(userToken -> userToken.getUserId() != null)
                .switchIfEmpty(getRefreshTokenException(String.format("Token with id %s does not exist", tokenId), HttpStatus.UNAUTHORIZED));
    }

    public Mono<UserToken> getUserTokenByRefreshToken(String refreshToken){
        return userTokenRepository.getUserTokenByRefreshToken(refreshToken)
                .filter(userToken -> userToken.getTokenId() != null)
                .switchIfEmpty(getRefreshTokenException("Token not exist", HttpStatus.UNAUTHORIZED));
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


    public Mono<Void> changePasswordForRecovery(Long version, String password, Instant expiredDate, String userCode){
            if (expiredDate.isBefore(Instant.now())) {
                return userRepository.clearRecoveryCode(userCode, version)
                        .flatMap(newVersion -> Mono.empty())
                        .then(Mono.error(new RecoveryCodeExpiredException("Recovery code expired")));//send new version to users;
            }
            else {
                String bcryptPassword = springSecurityConfig.bCryptPasswordEncoder().encode(password);
                return userRepository.changePassword(userCode, bcryptPassword, version)
                        .filter(newVersion -> newVersion!=0)
                        .switchIfEmpty(getNotFoundException("Password already changed"))
                        .flatMap(newVersion -> Mono.empty()); //отослать новые версии подписчикам

            }
    }

    public Mono<Long> simpleChangePassword(Long version, String userCode, String password){
        String bcryptPassword = springSecurityConfig.bCryptPasswordEncoder().encode(password);
        return userRepository.changePassword(userCode, bcryptPassword, version)
                .filter(newVersion -> newVersion !=0)
                .switchIfEmpty(userRepository.getUserByUserCode(userCode)
                        .flatMap(user -> getVersionException(user.getVersion())));
    }

    public Mono<Void> removeUserToken(String tokenId){
        return userTokenRepository.getUserTokenByTokenIdForDeleteToken(tokenId)
                        .then(userTokenRepository.removeUserTokenByTokenId(tokenId)
                                .filter(res -> res!=0)
                                .switchIfEmpty(getRefreshTokenException("Token already removed", HttpStatus.NOT_FOUND))
                                .flatMap(res -> Mono.empty()));
    }

    public Mono<Void> removeAllUserTokens(String tokenId){
        return getUserTokenByTokenId(tokenId, false)
                        .flatMap(userToken -> userTokenRepository.removeAllUserTokens(tokenId, userToken.getUserId())
                                .onErrorResume(e->getRefreshTokenException("Tokens already removed", HttpStatus.UNAUTHORIZED))
                                .flatMap(res -> Mono.empty()));
    }


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
                .switchIfEmpty(getNotFoundException("Secret code not found for user, enable two factor status"))
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
                            return Mono.just(new TokensResponse(tokens.getT1(), tokens.getT2().getRefreshToken(), tokens.getT2().getTokenId(), user.getVersion()));
                        }));
    }


    public Mono<Map<String, String>> updatePairTokens(String refreshToken, Long userId, Boolean checkResult){
        if (!checkResult)
            return userTokenRepository.removeUserTokenByRefreshToken(refreshToken)
                    .then(getRefreshTokenException("Token expired", HttpStatus.UNAUTHORIZED));

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

    private <T> Mono<T> getRefreshTokenException(String message, HttpStatus httpStatus){
        if (httpStatus.equals(HttpStatus.UNAUTHORIZED))
             return Mono.error(new RefreshTokenException(message));
        else
            return Mono.error(new RefreshTokenException(message, httpStatus));
    }

    private <T> Mono<T> getRequestException(String message){
        return Mono.error(new InvalidRequestException(message));
    }

    private <T> Mono<T> getNotFoundException(String message){
        return Mono.error(new NotFoundException(message));
    }

}
