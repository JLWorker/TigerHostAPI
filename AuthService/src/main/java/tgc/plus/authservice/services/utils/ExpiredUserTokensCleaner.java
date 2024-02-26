package tgc.plus.authservice.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.repository.UserTokenRepository;

import java.util.concurrent.TimeUnit;

@Component
public class ExpiredUserTokensCleaner {

    @Autowired
    UserTokenRepository userTokenRepository;

    @Scheduled(timeUnit = TimeUnit.DAYS ,fixedDelayString = "${scheduler.tokens.cleaner.delay.ms}")
    public Mono<Void> cleaner(){
        return userTokenRepository.remoteUserTokenByTime();
    }

}
