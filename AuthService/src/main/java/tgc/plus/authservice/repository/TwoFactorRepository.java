package tgc.plus.authservice.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.entities.TwoFactor;

public interface TwoFactorRepository extends ReactiveCrudRepository<TwoFactor, Long> {

    Mono<TwoFactor> getTwoFactorByDeviceToken(String deviceToken);

    Mono<Integer> removeTwoFactorByDeviceToken(String deviceToken);

    @Modifying
    @Query("DELETE FROM two_factor WHERE create_date < now() - (interval '1 second' * :seconds)")
    Mono<Void> remoteTwoFactorsByTime(Integer seconds);

}
