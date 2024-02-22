package tgc.plus.authservice.services.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.standard.TemporalAccessorParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.repository.TwoFactorRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;

@Component
public class Expired2FaCleaner {

    @Autowired
    TwoFactorRepository twoFactorRepository;

    @Value("${jwt.2fa.access.expired}")
    public Integer access2FaExpiredDate;

    @Scheduled(fixedDelayString = "${scheduler.cleaner.delay}")
    private Mono<Void> cleaner(){
        return twoFactorRepository.remoteTwoFactorsByTime(access2FaExpiredDate/1000);
    }

}
