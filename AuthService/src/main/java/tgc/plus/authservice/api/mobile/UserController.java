package tgc.plus.authservice.api.mobile;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
    public Mono<TokensResponse> registration(@RequestBody @Valid UserRegistration regData, ServerHttpRequest serverHttpRequest) {
        return userFacade.registerUser(regData, Objects.requireNonNull(serverHttpRequest.getRemoteAddress()).getAddress().getHostAddress());
    }

    @PostMapping("/login")
    public Mono<TokensResponse> login(@RequestBody @Valid UserLogin logData, ServerHttpRequest serverHttpRequest) {
        return userFacade.loginUser(logData, Objects.requireNonNull(serverHttpRequest.getRemoteAddress()).getAddress().getHostAddress());
    }

    @PutMapping("/phone")
    public Mono<UserChangeContactResponse> changePhone(@RequestBody @Valid @JsonView(UserChangeContacts.ChangePhone.class) UserChangeContacts userChangeContacts){
        return userFacade.changePhone(userChangeContacts);
    }

    @PutMapping("/email")
    public Mono<UserChangeContactResponse> changeEmail(@RequestBody @Valid @JsonView(UserChangeContacts.ChangeEmail.class) UserChangeContacts userChangeContacts){
        return userFacade.changeEmail(userChangeContacts);
    }

//    @GetMapping("/info")
//    public Mono<UserInfoResponse> userInfoResponseMono(){
//        return userFacade.getInfoAboutAccount();
//    }

    @PutMapping("/recovery")
    public Mono<Void> createRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Restore.class) RestorePassword restorePassword){
        return userFacade.generateRecoveryCode(restorePassword);
    }

    @PutMapping("/check")
    public Mono<Void> checkRecoveryCode(@RequestBody @Valid @JsonView(RestorePassword.Check.class) RestorePassword restorePassword){
        return userFacade.checkRecoveryCode(restorePassword);
    }

}
