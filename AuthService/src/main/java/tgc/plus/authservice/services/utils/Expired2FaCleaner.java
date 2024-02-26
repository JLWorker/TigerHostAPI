package tgc.plus.authservice.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.repository.TwoFactorRepository;

@Component
public class Expired2FaCleaner {

    @Autowired
    TwoFactorRepository twoFactorRepository;

    @Value("${jwt.2fa.access.expired.ms}")
    public Integer access2FaExpiredDate;

    @Scheduled(fixedDelayString = "${scheduler.cleaner.delay.ms}")
    private Mono<Void> cleaner(){
        return twoFactorRepository.remoteTwoFactorsByTime(access2FaExpiredDate/1000);
    }

}
