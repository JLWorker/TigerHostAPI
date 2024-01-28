package tgc.plus.authservice.api.mobile;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tgc.plus.authservice.dto.user_dto.*;
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
    public Mono<RegistrationTokens> registration(@RequestBody @Valid UserRegistration regData, ServerHttpRequest serverHttpRequest) {
        return userFacade.registerUser(regData, Objects.requireNonNull(serverHttpRequest.getRemoteAddress()).getAddress().getHostAddress());
    }

    @PostMapping("/login")
    public Mono<RegistrationTokens> login(@RequestBody @Valid UserLogin logData, ServerHttpRequest serverHttpRequest) {
        return userFacade.loginUser(logData, Objects.requireNonNull(serverHttpRequest.getRemoteAddress()).getAddress().getHostAddress());
    }

    @PutMapping("/phone")
    public Mono<Long> changePhone(@RequestBody @JsonView(value = UserChange.ChangePhone.class) @Valid UserChange userChange, ServerHttpRequest serverHttpRequest){
        return userFacade.changePhone(userChange, String.valueOf(serverHttpRequest.getHeaders().get("Authorization")));
    }

}
