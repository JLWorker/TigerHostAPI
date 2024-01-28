package tgc.plus.authservice.services;

import io.jsonwebtoken.*;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.entity.UserDetail;
import tgc.plus.authservice.exceptions.exceptions_clases.AccessTokenExpiredException;
import tgc.plus.authservice.repository.UserTokenRepository;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class TokenService {

    @Value("${jwt.token.expired}")
    public Long accessExpireDate;

    @Value("${jwt.refresh.expired}")
    public Long refreshExpireDate;


    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    public Mono<String> createAccessToken(String userCode, String role) {
        return Mono.defer(() -> {
            Instant now = Instant.now();
            Instant expirationDate = now.plusMillis(accessExpireDate);
            Claims claims = Jwts.claims().add(Map.of("user_code", userCode, "role", role)).build();
            String accessToken = Jwts.builder()
                    .claims(claims)
                    .expiration(Date.from(expirationDate))
                    .issuedAt(Date.from(now))
                    .signWith(springSecurityConfig.getSecretKey())
                    .compact();
            return Mono.just(accessToken);
        });
    }

    public Mono<tgc.plus.authservice.entity.UserToken> createRefToken(Long userId) {
            return generateTokenId().flatMap(tokenId -> {
                String refreshToken = UUID.randomUUID().toString();
                Instant startDate = Instant.now();
                Instant finishDate = startDate.plusMillis(refreshExpireDate);
                return userTokenRepository.save(new tgc.plus.authservice.entity.UserToken(tokenId, userId, refreshToken, startDate, finishDate));
            });
        }

    private Mono<String> generateTokenId(){
       return Mono.defer(()->{
           int randInt = ThreadLocalRandom.current().nextInt(1000, 10000);
           return Mono.just("ID-" + Instant.now().getNano() + randInt);
       });
    }


    public Mono<Boolean> checkAccessToken(String token) throws AccessTokenExpiredException {
        return Mono.defer(() -> {
            try {
                Jws<Claims> claims = Jwts.parser().decryptWith(springSecurityConfig.getSecretKey()).build().parseSignedClaims(token);
                return Mono.just(claims.getPayload().getExpiration().after(Date.from(Instant.now())));
            } catch (JwtException e) {
                return Mono.error(new InvalidRequestException("Invalid access token"));
            }
        });
    }

    public Mono<String> getUserCode(String token){
        return Mono.defer(() -> {
            try{
            Jws<Claims> claims = Jwts.parser().verifyWith(springSecurityConfig.getSecretKey()).build().parseSignedClaims(token);
            return Mono.just(claims.getPayload().get("user_code", String.class));
        } catch (JwtException e) {
                return Mono.error(new JwtException(e.getMessage()));
            }
        });
    }



//    public boolean checkRefreshToken(String username) throws TokenException{
//        User user = userRepository.findUserByUserName(username);
//        Date expired = user.getRefreshToken().getExpiredDate();
//        if(expired.before(new Date())){
//            return false;
//        }
//        else{
//            return true;
//        }
//    }


}
