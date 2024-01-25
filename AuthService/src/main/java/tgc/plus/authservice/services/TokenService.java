package tgc.plus.authservice.services;

import io.jsonwebtoken.*;
import io.netty.util.internal.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.configs.SpringSecurityConfig;
import tgc.plus.authservice.exceptions.AccessTokenException;
import tgc.plus.authservice.repository.UserTokenRepository;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenService {

    @Value("${jwt.token.expired}")
    public Long expireDate;

    @Autowired
    UserTokenRepository userTokenRepository;

    @Autowired
    SpringSecurityConfig springSecurityConfig;

    public Mono<String> createAccessToken(String userCode, String role) {
        return Mono.defer(() -> {
            Instant now = Instant.now();
            Instant expirationDate = now.plusMillis(expireDate);
            Claims claims = Jwts.claims().subject(userCode).build();
            claims.put("role", role);
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
                Instant finishDate = startDate.plusMillis(expireDate);
                return userTokenRepository.save(new tgc.plus.authservice.entity.UserToken(tokenId, userId, refreshToken, Date.from(startDate), Date.from(finishDate)));
            });
        }

    private Mono<String> generateTokenId(){
       return Mono.defer(()->{
           int randInt = ThreadLocalRandom.current().nextInt(1000, 10000);
           return Mono.just("ID-" + Instant.now().getNano() + randInt);
       });
    }


    public Mono<Boolean> checkAccessToken(String token) throws AccessTokenException {
        return Mono.defer(() -> {
            try {
                Jws<Claims> claims = Jwts.parser().decryptWith(springSecurityConfig.getSecretKey()).build().parseSignedClaims(token);
                return Mono.just(claims.getPayload().getExpiration().after(Date.from(Instant.now())));
            } catch (JwtException e) {
                return Mono.error(new AccessTokenException("Invalid access token"));
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
