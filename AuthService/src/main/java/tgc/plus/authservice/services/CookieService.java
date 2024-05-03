package tgc.plus.authservice.services;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.UserToken;
import tgc.plus.authservice.exceptions.exceptions_elements.auth_exceptions.RefreshTokenException;
import tgc.plus.authservice.facades.utils.utils_enums.CookiePayload;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

@Service
public class CookieService {

    @Value("${cookie.secret}")
    private String cookieSecret;

    @Value("${cookie.hmac}")
    private String cookieHmacSecret;

    @Value("${cookie.ref.domain}")
    private String refCookieDomain;

    private SecretKey cookiePayloadKey;
    private SecureRandom secureRandom;
    private Mac mac;
    private final String refreshTokenRegex = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";


    @SneakyThrows
    @PostConstruct
    void init(){
        cookiePayloadKey = new SecretKeySpec(cookieSecret.getBytes(), "AES");
        secureRandom = SecureRandom.getInstanceStrong();
        SecretKey cookieHmacKey = Keys.hmacShaKeyFor(cookieHmacSecret.getBytes());
        mac = Mac.getInstance(HmacAlgorithms.HMAC_SHA_256.getName());
        mac.init(cookieHmacKey);
    }

    public Mono<Void> addRefCookie(ServerHttpResponse serverHttpResponse, UserToken userToken){
        return encodeCookiePayload(userToken.getRefreshToken())
                .flatMap(cookiePayload -> {
                    ResponseCookie cookie = ResponseCookie.from(CookiePayload.REFRESH_TOKEN.name(), cookiePayload)
                            .httpOnly(true)
                            .domain(refCookieDomain)
                            .path("/api/auth/tokens/")
                            .secure(true)
                            .sameSite("Strict")
                            .maxAge(Duration.between(userToken.getCreateDate(), userToken.getExpiredDate()))
                            .build();
                    serverHttpResponse.addCookie(cookie);
                    return Mono.empty();
                });
    }

    private Mono<String> encodeCookiePayload(String refreshToken){
        return Mono.defer(() -> {
            try {
                Mac macClone = (Mac) mac.clone();
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                byte[] randomNumber = new byte[16];
                secureRandom.nextBytes(randomNumber);
                IvParameterSpec ivParameterSpec = new IvParameterSpec(randomNumber);
                cipher.init(Cipher.ENCRYPT_MODE, cookiePayloadKey, ivParameterSpec);
                String cookieRefresh = Base64.getEncoder().encodeToString(randomNumber) + Base64.getEncoder().encodeToString(cipher.doFinal(refreshToken.getBytes()));
                String hmacCookie = Base64.getEncoder().encodeToString(macClone.doFinal(cookieRefresh.getBytes()));
                return Mono.just(String.format("%s.%s", cookieRefresh, hmacCookie));
            }
            catch (Exception e){
                return Mono.error(new RuntimeException(e));
            }
        });
    }

    public Mono<String> getRefreshFromCookie(String cookieData){
            try {
                IvParameterSpec parameterSpec = new IvParameterSpec(Base64.getDecoder().decode(cookieData.substring(0, 24).getBytes()));
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, cookiePayloadKey, parameterSpec);
                String refreshToken = new String(cipher.doFinal(Base64.getDecoder().decode(cookieData.substring(24, cookieData.indexOf(".")).getBytes())));
                return (refreshToken.matches(refreshTokenRegex)) ? Mono.just(refreshToken) : Mono.error(new RefreshTokenException("Refresh token not exist"));
            }
            catch (Exception e){
                return Mono.error(new RuntimeException(e));
            }
    }


}
