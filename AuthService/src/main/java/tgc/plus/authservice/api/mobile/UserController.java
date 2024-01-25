package tgc.plus.authservice.api.mobile;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.UserData;
import tgc.plus.authservice.dto.user_dto.UserRegistration;
import tgc.plus.authservice.dto.user_dto.RegistrationTokens;
import tgc.plus.authservice.facades.UserFacade;

import java.util.Objects;

@RestController
@RequestMapping("/account")
@Slf4j
public class UserController {

    //пишем регистрацию и логин, чтобы просто возвращали данные (релизуем генерацию токенов и их возврат + Entity)
    //восстановление пароля тоже пилим
    //далее настраиваем security, а после допиливаем все что с нима связанно (удаление и прочее)

    @Autowired
    UserFacade userFacade;

    @PostMapping("/reg")
    public Mono<RegistrationTokens> registration(@RequestBody UserRegistration regData, ServerHttpRequest serverHttpRequest) {
        return userFacade.registerAccount(regData, Objects.requireNonNull(serverHttpRequest.getLocalAddress()).toString());

    }

}
