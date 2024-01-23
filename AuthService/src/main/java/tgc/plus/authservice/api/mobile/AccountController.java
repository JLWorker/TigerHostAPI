package tgc.plus.authservice.api.mobile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.account_controller_dto.RegDto;
import tgc.plus.authservice.dto.account_controller_dto.TokensDataDto;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

    //пишем регистрацию и логин, чтобы просто возвращали данные (релизуем генерацию токенов и их возврат + Entity)
    //восстановление пароля тоже пилим
    //далее настраиваем security, а после допиливаем все что с нима связанно (удаление и прочее)

    @PostMapping("/reg")
    public Mono<TokensDataDto> registration(@RequestBody Mono<RegDto> regData){
        return regData.flatMap(el -> {
            log.info(el.getDeviceDataDto().getName() + " " + el.getUserDataDto().getEmail());
//            return Mono.error(new RuntimeException("We have problem"));
            return Mono.just(new TokensDataDto("264738", "8492021"));
        });
//                .doOnSuccess(el -> log.info("Success"))
//                .doOnError(er -> log.error(er.getMessage()));
    }

}
