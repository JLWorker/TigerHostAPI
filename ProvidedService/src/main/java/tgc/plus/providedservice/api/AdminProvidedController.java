package tgc.plus.providedservice.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/provided/admin")
public class AdminProvidedController {

    @PostMapping("/cr_tariff")
    public Mono<Void> createTariff(){
        return Mono.empty();
    }
}
